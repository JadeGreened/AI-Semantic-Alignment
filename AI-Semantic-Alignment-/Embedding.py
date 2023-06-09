import openai
import os
import re
import requests
import sys
from num2words import num2words
import os
import pandas as pd
import numpy as np
from openai.cli import display
from openai.embeddings_utils import get_embedding, cosine_similarity
import tiktoken

API_KEY = os.getenv("AZURE_OPENAI_API_KEY")
RESOURCE_ENDPOINT = os.getenv("AZURE_OPENAI_ENDPOINT")

openai.api_type = "azure"
openai.api_key = API_KEY
openai.api_base = RESOURCE_ENDPOINT
openai.api_version = "2023-05-15"

url = openai.api_base + "/openai/deployments?api-version=2023-05-15"

r = requests.get(url, headers={"api-key": API_KEY})



# df=pd.read_csv(os.path.join(os.getcwd(),'bill_sum_data.csv')) # This assumes that you have placed the bill_sum_data.csv in the same directory you are running Jupyter Notebooks
# df
#
# df_bills = df[['text', 'summary', 'title']]
# df_bills


# 打开文件并读取内容
with open('C:\\Users\\20245\\Desktop\\Scientific Research\\ontology.txt', 'r') as file:
    df = file.read()

# 处理文档内容
# 这里可以根据需要对文档内容进行进一步处理，例如分词、提取信息等

# 打印文档内容
print(df)

def normalize_text(s, sep_token=" \n "):
   return s.split(',')


df = normalize_text(df)



print(df)

tokenizer = tiktoken.get_encoding("cl100k_base")
# for things in df:
#   sample_encode = tokenizer.encode(things)
#   decode = tokenizer.decode_tokens_bytes(sample_encode)
#   print(decode)
# print(len(df))


embeddingSet = []

desktop_path = os.path.expanduser('~/Desktop')
file_path1 = os.path.join(desktop_path, 'Scientific Research\\embeddingSet1.txt')
file_path2 = os.path.join(desktop_path, 'Scientific Research\\ontology1.txt')

# 打开文件并写入字符串
with open(file_path1, 'w') as file1, open(file_path2, 'w') as file2:
    for string in df:
            series = pd.Series([string])
            embedding = series.apply(lambda x: get_embedding(x, engine='text-embedding-ada-002'))
            print(embedding[0])
            print(string)
            embeddingSet.append(embedding)

            # 将列表转换为字符串
            list_str1 = ','.join(map(str, embedding[0]))  # 修改这里，使用 map(str, ...) 将列表中的元素转换为字符串

            # 写入文件1
            file1.write(list_str1)
            file1.write('\n')  # 换行

            # 写入文件2
            file2.write(string)
            file2.write('\n')  # 换行


# 打印 embeddingSet
     # engine should be set to the deployment name you chose when you deployed the text-embedding-ada-002 (Version 2) model


# print(embeddingSet)
# file_path = 'D:\\WorkSpace\\embedding'  # 替换为你想要保存文件的完整路径

# 定义列表


import os

# 获取桌面路径


# 指定文件路径
# 指定文件路径
# file_path = 'C:\\Users\\20245\\Desktop\\Scientific Research\\embeddingSet.txt'  # 替换为你想要保存文件的完整路径
#
# # 将 embeddingSet 转换为字符串
# list_str = ', '.join(embeddingSet)
#
# # 打开文件并写入字符串
# with open(file_path, 'w') as file:
#     file.write(list_str)

# sample_encode = tokenizer.encode(df_bills.text[0])
# decode = tokenizer.decode_tokens_bytes(sample_encode)
# decode
# print(len(decode))
# df_bills['ada_v2'] = df_bills["text"].apply(lambda x : get_embedding(x, engine = 'text-embedding-ada-002')) # engine should be set to the deployment name you chose when you deployed the text-embedding-ada-002 (Version 2) model
# df_bills
#
# # search through the reviews for a specific product
# def search_docs(df, user_query, top_n=3, to_print=True):
#     embedding = get_embedding(
#         user_query,
#         engine="text-embedding-ada-002" # engine should be set to the deployment name you chose when you deployed the text-embedding-ada-002 (Version 2) model
#     )
#     df["similarities"] = df.ada_v2.apply(lambda x: cosine_similarity(x, embedding))
#
#     res = (
#         df.sort_values("similarities", ascending=False)
#         .head(top_n)
#     )
#     if to_print:
#         display(res)
#     return res
#
#
# res = search_docs(df_bills, "Can I get information on cable company tax revenue?", top_n=4)
# print(res["summary"][9])









