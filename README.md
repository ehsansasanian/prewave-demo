# Prewave Project

## Overview

This project provides a service for managing hierarchical data in a tree structure. It includes:

## Assumptions



## Setup

### Installation

1. Clone this repository:
   ```
   git clone git@gh_personal:ehsansasanian/prewave-demo.git 
   ```

2.1 Start the services with Docker Compose:
   ```
   docker-compose up -d
   ```

   This will:
   - Start a PostgreSQL database on port 5433
   - Configure the database with the credentials defined in docker-compose.yml

2.2 Alternatively, you can start the service manually via spring commands but you need to have the database running first.

## Building a Sample Tree

The project includes a script to build a sample tree structure by creating edges between nodes.

### How to Use the Tree Builder Script

1. Make sure your API service is running and accessible at http://localhost:8080
2. Run the script:
   ```
   ./scripts/build_tree.sh
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

## API Endpoints

- POST /api/edges - Create a new edge between nodes
  - Request body: `{"fromId": <source_node_id>, "toId": <target_node_id>}`
