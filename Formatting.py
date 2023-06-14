import openai
import os
import re
import requests
import sys
from num2words import num2words
import pandas as pd
import numpy as np
from openai.cli import display
from openai.embeddings_utils import get_embedding, cosine_similarity
import tiktoken
from qdrant_client.models import PointStruct
from qdrant_client import QdrantClient
from qdrant_client import QdrantClient

# Below code seems not using
# client = QdrantClient(path="C:\\Users\\20245\\Desktop\\Scientific Research\\db")  # Persists changes to disk

qdrant_client = QdrantClient(
   url=os.getenv("Qdrant_URL"),
   api_key=os.getenv("Qdrant_API_KEY"),
)

collection_name = "embeddingCollection"
openai.api_type = "azure"
openai.api_key = os.getenv("AZURE_OPENAI_API_KEY")
openai.api_base = os.getenv("AZURE_OPENAI_ENDPOINT")
openai.api_version = "2023-05-15"

# Below code seems not using
# url = openai.api_base + "/openai/deployments?api-version=2023-05-15"
# r = requests.get(url, headers={"api-key": API_KEY})

def normalizeText(filepath):
   text = ""
   with open(filepath, 'r', encoding='utf-8-sig') as file:
      df = file.read()
      text+=df
   pattern = r'(<owl:Class rdf:about="[^"]+">.*?</owl:Class>)'
   matches = re.findall(pattern, text, re.DOTALL)
   return matches

def turnIntoList(s):
   return s.split(',')

def getEmbeddings(list):
   embeddingSet = []
   ontologySet = []
   for string in list:
      series = pd.Series([string])
      embedding = series.apply(lambda x: get_embedding(x, engine='text-embedding-ada-002'))
      embeddingSet.append(embedding[0])
      ontologySet.append(string)
      ontologySet = [s.replace(" ", "") for s in ontologySet]
   return embeddingSet

def sentToServer(payload, vectors, collection_name,qdrant_client):
   qdrant_client.delete_collection(collection_name="{embeddingCollection}")
   qdrant_client.upsert(
      collection_name=collection_name,
      points=[
         PointStruct(
            id=idx,
            vector=embedding,
            payload={"ontologySet": ontology},
         )
         for idx, (embedding, ontology) in enumerate(zip(vectors, payload))
      ]
   )
   return


