# Store API

---
## Table of Contents

1. [Technologies](#technologies)
2. [Getting Started](#getting-started)
3. [SQL Schema Documentation](#sql-schema-documentation)
    - [Tables Overview](#tables-overview)
    - [Relationships Between Tables](#relationships-between-tables)
5. [API Documentation](#api-documentation)
    - [Create Product](#create-product)
    - [Delete Product](#delete-product)
    - [Update Product](#update-product)
    - [Get Products](#get-products)
    - [Error Handling](#error-handling)
6. [Improvements](#improvements)

---
## **Technologies**

This project uses the following technologies:

- Java JDK 12 or later
- IDE (e.g., IntelliJ IDEA, Eclipse)
- Docker
- MySql
- Spring boot
- H2 Database (for testing)
---

## Getting Started

### Running the Application Locally

To get the application up and running on your local machine, follow the steps below.

### 1. Build the Project

Before running the application, make sure everything is set up correctly by building the project. You can do this using the following command:

```bash
./gradlew clean build
```

This will clean any previous builds and create a fresh build of the project.

### 2. Set Up the Database

The application requires a database to be up and running. To set it up, we'll use Docker to create the necessary environment. You can set up the database with the following command:

```bash
docker compose up
```

This command will pull the necessary Docker images and start the database service defined in your `docker-compose.yml` file.

### 3. Tear Down the Database (Optional)

If you want to stop the database and remove the volumes (e.g., to reset the environment), you can tear down the database with the following command:

```bash
docker compose down -v
```

This will stop and remove all containers, networks, and volumes defined in the Docker Compose file.

### 4. Run the Application

Once the database is set up, you can start the application with the following command:

```bash
./gradlew bootRun
```

This will launch the application, and it should be accessible locally.

---
## **SQL Schema Documentation**

### **Tables Overview**

#### 1. **`products` Table**

Stores detailed information about each product.

| Column Name                  | Data Type       | Description                                                                         |
| ---------------------------- | --------------- | ----------------------------------------------------------------------------------- |
| `id`                         | `BIGINT`        | The primary key for each product record. Used to uniquely identify products.        |
| `title`                      | `VARCHAR(100)`  | The title/name of the product. Must be unique.                                      |
| `distribution`               | `VARCHAR(10)`   | The distribution method (e.g., digital, physical).                                  |
| `format`                     | `VARCHAR(20)`   | The format of the product (e.g., MP3, WAV, VINYL).                                  |
| `currency`                   | `VARCHAR(3)`    | The currency in which the product's price is listed (e.g., USD, EUR).               |
| `price`                      | `DECIMAL(10,2)` | The price of the product, with two decimal places.                                  |
| `release_date`               | `TIMESTAMP`     | The date and time when the product was released. Defaults to the current timestamp. |
| `store_name`                 | `VARCHAR(100)`  | The name of the store or platform selling the product.                              |
| `product_group_title`        | `VARCHAR(100)`  | The title of the product group this product belongs to.                             |
| `product_group_release_date` | `TIMESTAMP`     | The release date of the product group. Defaults to the current timestamp.           |

#### Notes:
- `id` is the primary key.
- `title` is a unique constraint, ensuring no two products can have the same title.
- `release_date` and `product_group_release_date` are automatically set to the current timestamp when the record is created.

---

### 2. **`products_seq` Table**

Used to track the next value for the `id` column of the `products` table (a custom sequence manager).

| Column Name | Data Type | Description                                                   |
|-------------|-----------|---------------------------------------------------------------|
| `next_val`  | `BIGINT`  | Stores the next value to be used for generating the `id` in the `products` table. |

#### Notes:
- The `next_val` is manually updated by inserting new values. The initial value is set to 1.

---

### 3. **`tags` Table**

Stores a list of tags that can be associated with products. Tags help categorize or label products for easy searching and filtering.

| Column Name | Data Type     | Description                                                    |
| ----------- | ------------- | -------------------------------------------------------------- |
| `id`        | `BIGINT`      | The primary key for each tag. Used to uniquely identify a tag. |
| `name`      | `VARCHAR(50)` | The name of the tag and must be unique                         |

#### Notes:
- `id` is the primary key.
- `name` is unique to ensure no duplicate tags are created.

---

### 4. **`tags_seq` Table**

Used to track the next value for the `id` column of the `tags` table (a custom sequence manager).

| Column Name | Data Type | Description                                                   |
|-------------|-----------|---------------------------------------------------------------|
| `next_val`  | `BIGINT`  | Stores the next value to be used for generating the `id` in the `tags` table. |

#### Notes:
- Like `products_seq`, `tags_seq` manages the sequence for generating tag IDs.
- The initial value is set to 1.

---

### 5. **`product_tags` Table**

This is a junction table that associates products with tags, enabling many-to-many relationships between products and tags.

| Column Name | Data Type  | Description                                                   |
|-------------|------------|---------------------------------------------------------------|
| `product_id`| `BIGINT`   | The `id` of the product from the `products` table. Foreign key referencing `products(id)`. |
| `tag_id`    | `BIGINT`   | The `id` of the tag from the `tags` table. Foreign key referencing `tags(id)`. |

#### Notes:
- The combination of `product_id` and `tag_id` is the primary key.
- Both `product_id` and `tag_id` are foreign keys referencing the `products` and `tags` tables, respectively.
- When a product or tag is deleted, the corresponding rows in `product_tags` are automatically deleted due to the `ON DELETE CASCADE` constraint.

---

### **Relationships Between Tables**

- **`products` and `tags`**:
    - A product can have many tags and a tag can be associated with many products.
    - The relationship is represented by the **`product_tags`** junction table.

- **`products_seq` and `products`**:
    - `products_seq` manages the sequence for auto-incrementing `id` values in the `products` table.

- **`tags_seq` and `tags`**:
    - `tags_seq` manages the sequence for auto-incrementing `id` values in the `tags` table.

---
## **API Documentation**

This API provides CRUD operations for managing products in the system. It supports creating, updating, deleting, and retrieving products, as well as filtering products based on various parameters.

### **1.  Create Product**

**POST** `/api/product/create`

#### Request Body:
```json
{ 
	"title": "Amazing Product", 
	"isPhysical": true, 
	"format": "MP3", 
	"currency": "USD", 
	"price": "19.99", 
	"releaseDate": "2024-11-01T12:00:00Z", 
	"storeName": "Example Store",
	"productGroupTitle": "Example Product Group", 
	"productGroupReleaseDate": "2024-10-01T12:00:00Z", 
	"tags": [ "sale", "new-release" ] 
}
```

#### Response:
**200 OK**
```json
{
  "id": 123,
  "title": "Amazing Product",
  "distribution": "digital",
  "format": "MP3",
  "currency": "USD",
  "price": 19.99,
  "releaseDate": "2024-11-01T12:00:00Z",
  "storeName": "Example Store",
  "productGroupTitle": "Example Product Group",
  "productGroupReleaseDate": "2024-10-01T12:00:00Z",
  "tags": [ "sale", "new-release" ]
}
```

---

### **2.  Delete Product**

**DELETE** `/api/product/{id}/delete`
#### URL Parameters:

| **Field** | **Description**                                                                        | **Data Type**  | **Example** |
| --------- | -------------------------------------------------------------------------------------- | -------------- | ----------- |
| **id**    | The ID of the product to be deleted. This is passed as a **path variable** in the URL. | Long (Numeric) | `123`       |
#### Response:
**204 No Content**

---

### **3.  Update Product**

**PUT** `/api/product/{id}`
#### URL Parameters:
- **id** (Path Variable): The ID of the product to be updated.

#### Request Body:
```json{
  "title": "Updated Amazing Product",
  "isPhysical": false,
  "format": "VINYL",
  "currency": "USD",
  "price": "24.99",
  "releaseDate": "2024-11-15T12:00:00Z",
  "storeName": "Updated Example Store",
  "productGroupTitle": "Updated Product Group",
  "productGroupReleaseDate": "2024-10-15T12:00:00Z",
  "tags": [ "new-tag" ] // Overwrites tags
}
```

#### Response:
**200 OK**
```json
{
  "id": 123,
  "title": "Updated Amazing Product",
  "distribution": "physical",
  "format": "VINYAL",
  "currency": "USD",
  "price": 24.99,
  "releaseDate": "2024-11-15T12:00:00Z",
  "storeName": "Updated Example Store",
  "productGroupTitle": "Updated Product Group",
  "productGroupReleaseDate": "2024-11-15T12:00:00Z",
  "tags": [ "new-tag" ]
}
```

---

### **4. Get Products**

**GET** `/api/products`

Retrieves a list of products that match the provided filter criteria.

#### Query Parameters:
| **ield Name**               | **Description**                                                          | **Data Type**  | **Example**               | **Default Value/Constraints** |
| --------------------------- | ------------------------------------------------------------------------ | -------------- | ------------------------- | ----------------------------- |
| `store_name`                | The name of the store or platform selling the product.                   | `String`       | `"Example Store"`         | Optional                      |
| `group_title`               | The title of the product group.                                          | `String`       | `"Electronics"`           | Optional                      |
| `release_date_before`       | Filters products released before this date (exclusive).                  | `Instant`      | `"2024-01-01T00:00:00Z"`  | Optional                      |
| `release_date_after`        | Filters products released after this date (exclusive).                   | `Instant`      | `"2024-01-01T00:00:00Z"`  | Optional                      |
| `title`                     | Filters products by their title.                                         | `String`       | `"Awesome Product"`       | Optional                      |
| `group_release_date_before` | Filters products by their product group's release date before this date. | `Instant`      | `"2024-01-01T00:00:00Z"`  | Optional                      |
| `group_release_date_after`  | Filters products by their product group's release date after this date.  | `Instant`      | `"2024-01-01T00:00:00Z"`  | Optional                      |
| `tags`                      | Filters products by tags.                                                | `List<String>` | `["sale", "new-release"]` | Optional                      |
| `page`                      | Specifies the page number for pagination (1-based index).                | `Integer`      | `1`                       | Default: `1`                  |
| `size`                      | Specifies the number of results per page for pagination.                 | `Integer`      | `10`                      | Default: `10`                 |

#### Response:
**200 OK**
```json
[
  {
    "id": 123,
    "title": "Amazing Product",
    "distribution": "digital",
    "format": "MP3",
    "currency": "USD",
    "price": 19.99,
    "releaseDate": "2024-11-01T12:00:00Z",
    "storeName": "Example Store",
    "productGroupTitle": "Example Product Group",
    "productGroupReleaseDate": "2024-10-01T12:00:00Z",
    "tags": [
      "sale",
      "new-release"
    ]
  },
  {
    "id": 124,
    "title": "Amazing Product 2",
    "distribution": "digital",
    "format": "MP3",
    "currency": "USD",
    "price": 19.99,
    "releaseDate": "2024-11-01T12:00:00Z",
    "storeName": "Example Store",
    "productGroupTitle": "Example Product Group",
    "productGroupReleaseDate": "2024-10-01T12:00:00Z",
    "tags": [
      "sale",
      "new-release"
    ]
  }
]
```


---

## Improvements

While the application can be run locally as described above, there are several potential improvements you can implement to enhance the resiliency, scalability, and performance of the API. Below are some of the key improvements to consider:

### 1.  **Caching Query Responses**

Caching can significantly improve the performance and reduce the load on the database for frequently accessed data. Instead of querying the database repeatedly for the same data, a caching layer could store the results for a set period of time and return the cached results for subsequent requests.

### 2.  **Authorisation and Authentication**

Protecting sensitive operation like creating or deleting products should be implemented using proper authentication techniques to protect resources.

### 3.  **Moving IDs to UUID for Scalability**

Currently, the system uses auto-incrementing integers as primary keys. While this approach works for small-scale applications, it can run into issues as the application grows, especially in distributed systems where multiple services or databases may try to generate IDs concurrently.

### 4.  **Dockerising the Application**

Containerising the system with docker could provide benefits, such as easier deployment, scalability, and environment consistency across development, testing, and production environments.

### 5. **More Testing**

Thorough testing is crucial for maintaining the reliability and stability of the system as it evolves. Implementing a robust testing strategy that includes  multiple strategies such as unit tests, integration tests, and non functional tests tests ensures that the API works as expected and can handle edge cases.

### 6. **Logging and Monitoring**

Better use of monitoring and logging mechanisms would allow better visibility and the ability to track the systems behaviour. 