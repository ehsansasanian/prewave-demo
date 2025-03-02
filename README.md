# Prewave Project

## Overview

This project provides a service for managing hierarchical data in a tree structure. It includes:

## Assumptions & Design Decisions

- **Testing:**
    - Tests do not cover all possible scenarios but are intended to demonstrate one way of implementing the
      functionality.

- **Delete Operation Assumptions:**
    - It was unclear how the delete functionality should operate. Two options were considered:
        1. Create a new tree by detaching the subtree.
        2. Remove the edge and reassign the children of the deleted node (`to_id`) to the parent (`from_id`).
    - The implementation follows the latter approach.

- **Edge Representation & Constraints:**
    - An *Edge* represents a connection between two companies in the supply chain.
    - No foreign key constraints have been implemented as they were not requested.
    - For simplicity, the implementation assumes a single supply chain tree. This design can be extended to support
      multiple trees if required.
    - The root node is initialized as `from_id = -1` and `to_id = 1` (see `DemoApplication.kt` for details).

## Setup

### Prerequisites

- Docker
- Docker Compose

### Installation

1. Clone this repository: `git clone git@gh_personal:ehsansasanian/prewave-demo.git`

2. Start the services with Docker Compose: `docker-compose up -d` – this will:

- Start a PostgreSQL database on port 5433
- Starts application on port 8080

3. (Optional) Alternatively, you can start the service manually via spring commands but you need to have the database
   running
   first.

## Testing

* If you will be using a browser, please be careful with the browser asking for favicon – requesting it might result seeing some error messages in the
   console as it's added to the project

1. Make sure your API service is running and accessible at http://localhost:8080 via:

```
curl 'http://localhost:8080/'
```

2. POST `/api/edges` - Creates a new edge – expected response: `201 Created`

    ```
   curl --location 'localhost:8080/api/edges' \
    --header 'Content-Type: application/json' \
    --data '{
    "fromId": 1,
    "toId": 40
    }'
   ```

   Also, the project includes a script to build a sample tree structure by creating edges between nodes.

   ```
   ./scripts/build_tree.sh
   ```
3. GET `/api/edges/{node_id}/tree` - Retrieve all edges – expected response: `200 OK` with data in JSON format

    ```
    curl 'http://localhost:8080/api/edges/1/tree'
    ```

   Or if you have `jq` :

    ```
    curl 'http://localhost:8080/api/edges/1/tree' | jq
    ```

* To achieve memory efficiency, Postgres native recursive function with `depth is used to retrieve the tree structure. However, if the number of nodes also grow horizontally, for very big trees this might lead to memory issues. In this case, a cursor based query might be a better candidate to fetch the data in chunks which has better memory management.

4. DELETE `/api/edges/{from_id}/{to_id}` - Delete an edge

    ```
    curl -X DELETE 'http://localhost:8080/api/edges/1/2'
    ```

### Sample Tree Structure

The script creates the following tree structure:

```
             1
          /  |  \  \
         2   3   4  5
       / | \ \ | \ / | \   \
      6  7  8 9 10 11 12 13 14 15 16
                |        |
               / \       |
             17  18     19
             |
            20
```

### Further Considerations

Please note due to time constraints, the project has a poor test coverage. This might lead to some unexpected issues. But of course, I am happy to discuss the project further and improve it if needed.