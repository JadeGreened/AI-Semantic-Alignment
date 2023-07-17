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
from openai.embeddings_utils import get_embedding, cosine_similarity
from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams

qdrant_client = QdrantClient(
    url=os.getenv("Qdrant_URL"),
    api_key=os.getenv("Qdrant_API_KEY"),
)
API_KEY = os.getenv("AZURE_OPENAI_API_KEY")
RESOURCE_ENDPOINT = os.getenv("AZURE_OPENAI_ENDPOINT")
openai.api_type = "azure"
openai.api_key = API_KEY
openai.api_base = RESOURCE_ENDPOINT
openai.api_version = "2023-05-15"
url = openai.api_base + "/openai/deployments?api-version=2023-05-15"
r = requests.get(url, headers={"api-key": API_KEY})

app = Flask(__name__)

@app.route('/dataInfo',methods = ['POST','get'])
def showDatabase():
    data = request.get_data()
    collection_name = data.decode('utf-8')
    get_collection_info = qdrant_client.get_collection(collection_name=collection_name)
    print(type(get_collection_info))
    print(get_collection_info)
    return "done"

@app.route('/query', methods=['POST', 'get'])
def getSimilaVector():
    data = request.get_data()
    data_str = data.decode('utf-8')
    collection_name = data_str.split(',')[1]
    print(collection_name)
    query_vector = get_embedding(data_str.split(',')[0], engine='text-embedding-ada-002')
    print(query_vector)
    hits = qdrant_client.search(
        collection_name=collection_name,
        query_vector=query_vector,
        with_vectors=True,
        with_payload=True,
        limit=5  # Return 1 closest points
    )
    resultList = hits[0].payload['ontology']
    for i in range(1, len(hits)):
        resultList = resultList + ',' + hits[i].payload['ontology']
    return resultList


@app.route('/SendToDatabase', methods=['POST', 'get'])
def sending():
    data = request.get_data()
    data_str = data.decode('utf-8')
    idx = int(data_str.split(',')[0])
    ontology = data_str.split(',')[1]
    name = data_str.split(',')[2]
    embedding = get_embedding(ontology, engine='text-embedding-ada-002')
    qdrant_client.upsert(
        collection_name=name,
        points=[PointStruct(
            id=idx,
            vector=embedding,
            payload={"ontology": ontology})
        ]
    )
    return "上传成功！"


@app.route('/initiating', methods=['POST', 'get'])
def initiating():
    data = request.get_data()
    data_str = data.decode('utf-8')
    qdrant_client.delete_collection(collection_name="{embeddingCollection}")
    qdrant_client.recreate_collection(
        collection_name=data_str,
        vectors_config=VectorParams(size=1536, distance=Distance.COSINE),
    )
    return "初始化成功！"


@app.route('/toChatGPT', methods=['POST', 'get'])
def toChatGPT():
    data = request.get_data()
    data_str = data.decode('utf-8')
    response = openai.ChatCompletion.create(
        engine="gpt-35-turbo",  # engine = "deployment_name".
        messages=[
            {"role": "user", "content": data_str}
        ]
    )
    understanding = response["choices"][0]["message"]["content"]
    print(understanding)
    return understanding


@app.route('/receive', methods=['POST', 'get'])
def receive():
    mydata = request.get_data()
    mydata_str = mydata.decode('utf-8')
    print(mydata_str)
    return mydata_str


app.run(host="0.0.0.0", port=8080, debug=True)
