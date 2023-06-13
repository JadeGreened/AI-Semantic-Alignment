import openai
import requests
import os
from openai.embeddings_utils import get_embedding
from qdrant_client.models import PointStruct
from qdrant_client import QdrantClient
from qdrant_client import QdrantClient
qdrant_client = QdrantClient(
   url=os.getenv("Qdrant_URL"),
   api_key=os.getenv("Qdrant_API_KEY"),
)
collection_name = "embeddingCollection"

API_KEY = os.getenv("AZURE_OPENAI_API_KEY")
RESOURCE_ENDPOINT = os.getenv("AZURE_OPENAI_ENDPOINT")
openai.api_type = "azure"
openai.api_key = API_KEY
openai.api_base = RESOURCE_ENDPOINT
openai.api_version = "2023-05-15"

url = openai.api_base + "/openai/deployments?api-version=2023-05-15"

r = requests.get(url, headers={"api-key": API_KEY})

qdrant_client = QdrantClient(
   url=os.getenv("Qdrant_URL"),
   api_key=os.getenv("Qdrant_API_KEY"),
)

class Query:

    def __init__(self, name):
        self.name = name

    def getSimilaVector(self, query_text, collection_name, qdrant_client):
        query_vector = get_embedding(query_text, engine='text-embedding-ada-002')
        print(query_vector)
        hits = qdrant_client.search(
            collection_name=collection_name,
            query_vector=query_vector,
            with_vectors=True,
            with_payload=True,
            limit=1  # Return 1 closest points
        )
        return hits[0].payload['ontologySet']
