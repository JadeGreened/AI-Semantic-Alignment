from qdrant_client import QdrantClient
import os
import openai
import requests
import pandas as pd
import numpy as np
from openai.cli import display
from openai.embeddings_utils import get_embedding, cosine_similarity
import tiktoken

import to_Qdrant

API_KEY = os.getenv("AZURE_OPENAI_API_KEY")
RESOURCE_ENDPOINT = os.getenv("AZURE_OPENAI_ENDPOINT")

openai.api_type = "azure"
openai.api_key = API_KEY
openai.api_base = RESOURCE_ENDPOINT
openai.api_version = "2023-05-15"

url = openai.api_base + "/openai/deployments?api-version=2023-05-15"

r = requests.get(url, headers={"api-key": API_KEY})

import Formatting

  # Persists changes to disk

from qdrant_client import QdrantClient

qdrant_client = QdrantClient(
    url="https://2594c021-c2ef-45fc-be97-76a59675d361.us-east-1-0.aws.cloud.qdrant.io:6333",
    api_key="Yv5Na1gFga7Dy2YQ2UlsPsrC23JkhPB1fcTf9x7vpuaSYaedO5GtCQ",
)

# from qdrant_client.models import Distance, VectorParams
#
# client.recreate_collection(
#     collection_name="embeddingCollection",
#     vectors_config=VectorParams(size=1536, distance=Distance.COSINE),
# )
#
#
#
#
# import numpy as np
# from qdrant_client.models import PointStruct
#
# vectors = np.random.rand(100, 100)
# client.upsert(
#     collection_name="embeddingCollection",
#     points=[
#         PointStruct(
#             id=idx,
#             vector=vector.tolist(),
#             payload={"color": "red", "rand_number": idx % 10}
#         )
#         for idx, vector in enumerate(vectors)
#     ]
# )
#
# query_vector = np.random.rand(100)
# hits = client.search(
#     collection_name="my_collection",
#     query_vector=query_vector,
#     limit=5  # Return 5 closest points
# )
# print(hits)

FilePath = "D:\\WorkSpace\\projects\\AI-Semantic-Alignment-\\01_OntologiesTask11\\fiesta-iot.owl"
ontologies = Formatting.normalizeText(FilePath)
print(type(ontologies))
embeddingSet = Formatting.getEmbeddings(ontologies)
print(type(embeddingSet))
print(embeddingSet)
Formatting.sentToServer(ontologies, embeddingSet, "embeddingCollection", qdrant_client)
searcher = to_Qdrant.get_embedding("""<owl:Class rdf:about="&mthreelite;AirPollution">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">Usually measured using Air Quality Index (AQI), it is the measure of Air Pollution in the environment. It is similar to Air Quality.</rdfs:comment>
        <rdfs:label xml:lang="en">Air Pollution Quantity Kind</rdfs:label>
    </owl:Class>""", "embeddingCollection", qdrant_client)

