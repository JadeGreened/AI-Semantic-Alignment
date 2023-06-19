from flask import Flask, redirect, request, jsonify, app
import openai
import os
import re
import requests
import sys
import os
import pandas as pd
import numpy as np
from openai.cli import display
from openai.embeddings_utils import get_embedding, cosine_similarity
from qdrant_client.models import PointStruct
import tiktoken
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
collection_name = "embeddingCollection"


app = Flask(__name__)

@app.route('/query', methods=['POST', 'get'])
def getSimilaVector():
    my_json = request.get_json()
    query_text =my_json["ontology"]
    query_vector = get_embedding(query_text, engine='text-embedding-ada-002')
    print(query_vector)
    hits = qdrant_client.search(
        collection_name=collection_name,
        query_vector=query_vector,
        with_vectors=True,
        with_payload=True,
        limit=1  # Return 1 closest points
    )
    return jsonify(hits[0].payload['ontologySet'])





@app.route('/initiating', methods=['POST', 'get'])
def initiating():
    my_json = request.get_json()
    vectors = my_json["vectors"]
    payload = my_json["payload"]
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


    return jsonify(msg="上传成功！")



@app.route('/toChatGPT',methods=['POST', 'get'])
def toChatGPT():
    my_json = request.get_json()
    prompt = my_json["ontology"]
    response = openai.ChatCompletion.create(
        engine="gpt-35-turbo",  # engine = "deployment_name".
        messages=[
            {"role": "user", "content": prompt}
        ]
    )
    understanding = response["choices"][0]["message"]["content"]
    print(understanding)
    return jsonify(understanding=understanding)





app.run(host="0.0.0.0", port=8080, debug=True)
