# API Usage

## API
Unsplash API

Documentation:
https://unsplash.com/documentation

## Endpoint Example
https://api.unsplash.com/photos/random?count=10

## Headers
Authorization: Client-ID YOUR_ACCESS_KEY

## Example Response
[
  {
    "id": "abc123",
    "urls": {
      "regular": "https://images.unsplash.com/photo-xyz"
    },
    "user": {
      "name": "John Doe"
    },
    "description": "A beautiful landscape"
  }
]