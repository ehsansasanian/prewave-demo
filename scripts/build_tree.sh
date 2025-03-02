#!/bin/bash

API_URL="http://localhost:8080/api/edges"

create_edge() {
  local from_id=$1
  local to_id=$2
  
  echo "Creating edge from $from_id to $to_id..."
  
  response=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "{\"fromId\": $from_id, \"toId\": $to_id}")
  
  echo "Response: $response"
  echo
  
  sleep 0.25
}

echo "Building a tree ..."

# Level 1: Node 1 is the root
# Level 2: Nodes 2-5 are children of 1
create_edge 1 2
create_edge 1 3
create_edge 1 4
create_edge 1 5

# Level 3: Nodes 6-9 are children of 2
create_edge 2 6
create_edge 2 7
create_edge 2 8
create_edge 2 9

# Level 3: Nodes 10-12 are children of 3
create_edge 3 10
create_edge 3 11
create_edge 3 12

# Level 3: Nodes 13-15 are children of 4
create_edge 4 13
create_edge 4 14
create_edge 4 15

# Level 3: Node 16 is a child of 5
create_edge 5 16

# Level 4: Nodes 17-18 are children of 10
create_edge 10 17
create_edge 10 18

# Level 4: Node 19 is a child of 13
create_edge 13 19

# Level 5: Node 20 is a child of 17
create_edge 17 20

echo "Tree building completed!"
