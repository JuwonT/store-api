package org.juwont.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.juwont.domain.MediaFormat;
import org.juwont.entity.Product;
import org.juwont.entity.Tag;
import org.juwont.web.dto.CreateProductDTO;
import org.juwont.repository.ProductRepository;
import org.juwont.repository.TagRepository;
import org.juwont.web.dto.UpdateProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TagRepository tagRepository;

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        entityManager.createQuery("DELETE FROM Product").executeUpdate();
        entityManager.createQuery("DELETE FROM Tag").executeUpdate();
        entityManager.flush();
    }

    @Test
    void user_can_create_new_product() throws Exception {
        final CreateProductDTO createProductDTO = CreateProductDTO.builder()
                .title("A Tribe Called Quest - Midnight Marauders")
                .price("15.50")
                .currency("USD")
                .tags(List.of("Hip Hop"))
                .format(MediaFormat.MP3)
                .isPhysical(false)
                .productGroupTitle("Wu tang records")
                .storeName("Wu shop")
                .releaseDate(Instant.now())
                .productGroupReleaseDate(Instant.now())
                .build();

        final String content = objectMapper.writeValueAsString(createProductDTO);
        performRequest(HttpMethod.POST, "/api/product/create", content)
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "title": "A Tribe Called Quest - Midnight Marauders",
                          "distribution": "digital",
                          "format": "MP3",
                          "currency": "USD",
                          "price": 15.5,
                          "storeName": "Wu shop",
                          "productGroupTitle": "Wu tang records",
                          "tags": ["Hip Hop"]
                        }
                        """
                ));
    }

    @Test
    void rolls_back_db_entries_when_error_occurs_during_request() throws Exception {
        final Product product = productRepository.create(buildProductEntity());
        final Long id = product.getId();

        final CreateProductDTO createProductDTO = buildProductDTO().toBuilder()
                .tags(List.of("Folk", "Polka"))
                .build();

        final String content = objectMapper.writeValueAsString(createProductDTO);
        performRequest(HttpMethod.POST, "/api/product/create", content)
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "errorMessage":"Request contains an existing entity"
                        }
                        """
                ));

    }

    private static CreateProductDTO buildProductDTO() {
        return CreateProductDTO.builder()
                .title("A Tribe Called Quest - Midnight Marauders")
                .price("15.50")
                .currency("USD")
                .format(MediaFormat.MP3)
                .isPhysical(false)
                .productGroupTitle("Wu tang records")
                .storeName("Wu shop")
                .releaseDate(Instant.now())
                .productGroupReleaseDate(Instant.now())
                .build();
    }

    private static Product buildProductEntity() {
        return buildProductEntity(
                "A Tribe Called Quest - Midnight Marauders",
                "Tribal",
                "Native Tongues"
        );
    }

    private static Product buildProductEntity(final String title,
                                              final String storeName,
                                              final String groupTitle) {
        return Product.builder()
                .title(title)
                .price(new BigDecimal("15.50"))
                .currency(Currency.getInstance("USD"))
                .format(MediaFormat.MP3)
                .distribution("digital")
                .storeName(storeName)
                .productGroupTitle(groupTitle)
                .releaseDate(Instant.now())
                .productGroupReleaseDate(Instant.now())
                .tags(List.of())
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "store_name=Wu Store",
            "group_title=Wu Tang Records",
            "title=Wu Tang Clan - 36 Chambers"})
    void filters_products_by_strings(final String filter) throws Exception {
        productRepository.create(buildProductEntity());
        productRepository.create(buildProductEntity("Wu Tang Clan - 36 Chambers", "Wu Store", "Wu Tang Records"));
        productRepository.create(buildProductEntity("De La Soul = 3 Foot High", "Soul", "Brand Nubian"));

        performRequest(HttpMethod.GET, "/api/products?%s".formatted(filter))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("""
                        [
                            {
                                "title" : "Wu Tang Clan - 36 Chambers",
                                "distribution" : "digital",
                                "format" : "MP3",
                                "currency" : "USD",
                                "price" : 15.5,
                                "storeName" : "Wu Store",
                                "productGroupTitle": "Wu Tang Records",
                                "tags": []
                            }
                        ]
                        """
                ));

    }

    @Test
    void filters_products_by_tags() throws Exception {
        final Tag hipHopTag = tagRepository.create(new Tag("Hip Hop"));
        final Tag classicalTag = tagRepository.create(new Tag("Classical"));

        productRepository.create(buildProductEntity());
        productRepository.create(buildProductEntity("Wu Tang Clan - 36 Chambers", "Wu Store", "Wu Tang Records").toBuilder()
                .tags(List.of(hipHopTag))
                .build());
        productRepository.create(buildProductEntity("Kanye West - Late Registration", "Good Music", "Good Music").toBuilder()
                .tags(List.of(hipHopTag, classicalTag))
                .build());

        performRequest(HttpMethod.GET, "/api/products?tags=Hip Hop")
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("""
                        [
                          {
                            "title": "Wu Tang Clan - 36 Chambers",
                            "distribution": "digital",
                            "format": "MP3",
                            "currency": "USD",
                            "price": 15.50,
                            "storeName": "Wu Store",
                            "productGroupTitle": "Wu Tang Records",
                            "tags": [
                              "Hip Hop"
                            ]
                          },
                          {
                            "title": "Kanye West - Late Registration",
                            "distribution": "digital",
                            "format": "MP3",
                            "currency": "USD",
                            "price": 15.50,
                            "storeName": "Good Music",
                            "productGroupTitle": "Good Music",
                            "tags": [
                              "Hip Hop",
                              "Classical"
                            ]
                          }
                        ]
                        """
                ));

    }

    @Test
    void user_can_update_product_details() throws Exception {
        final Product product = productRepository.create(buildProductEntity());

        final UpdateProductDTO updateProductDTO = UpdateProductDTO.builder()
                .title("Adele - 30")
                .productGroupTitle("XL Records")
                .storeName("XL")
                .build();

        final String content = objectMapper.writeValueAsString(updateProductDTO);

        performRequest(HttpMethod.PUT, "/api/product/%s".formatted(product.getId()), content)
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "title": "Adele - 30",
                          "distribution": "digital",
                          "format": "MP3",
                          "currency": "USD",
                          "price": 15.5,
                          "storeName": "XL",
                          "productGroupTitle": "XL Records",
                          "tags": []
                        }
                        """
                ));
    }

    @Test
    void update_fails_when_request_object_is_not_valid() throws Exception {
        final Product product = productRepository.create(buildProductEntity());

        final UpdateProductDTO updateProductDTO = UpdateProductDTO.builder()
                .title("Adele - 30")
                .productGroupTitle("XL Records")
                .storeName("XL")
                .currency("CHY")
                .build();

        final String content = objectMapper.writeValueAsString(updateProductDTO);

        performRequest(HttpMethod.PUT, "/api/product/%s".formatted(product.getId()), content)
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "errorMessage":"Error on field (currency) : You can only choose currencies: GBP, EUR & USD"
                        }
                        """
                ));
    }

    @Test
    void user_can_delete_product() throws Exception {
        final Product product = productRepository.create(buildProductEntity());

        performRequest(HttpMethod.DELETE, "/api/product/%s/delete".formatted(product.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_fails_when_id_not_found() throws Exception {
        performRequest(HttpMethod.DELETE, "/api/product/%s/delete".formatted(1))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                          "errorMessage" : "Product: 1 does not exit"
                        }
                        """));
    }

    private ResultActions performRequest(final HttpMethod method,
                                         final String path) throws Exception {
        return mockMvc.perform(
                request(method, path)
                        .contentType(APPLICATION_JSON)
        ).andDo(print());
    }

    private ResultActions performRequest(final HttpMethod method,
                                         final String path,
                                         final String content) throws Exception {
        return mockMvc.perform(
                request(method, path)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        ).andDo(print());
    }

    private ResultActions performRequest(final HttpMethod method,
                                         final String path,
                                         final UUID accountId) throws Exception {
        return mockMvc.perform(
                request(method, path, accountId)
                        .contentType(APPLICATION_JSON)
        ).andDo(print());
    }
}