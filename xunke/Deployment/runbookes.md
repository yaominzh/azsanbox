[toc]
# build index
```json
PUT /shop?include_type_name=false
{
   "settings" : {
      "number_of_shards" : 1,
      "number_of_replicas" : 1
   },
   "mappings": {
     "properties": {
       "id":{"type":"integer"},
       "name":{"type":"text","analyzer": "ik_max_word","search_analyzer":"ik_smart"},
       "tags":{"type":"text","analyzer": "whitespace","fielddata":true},
       "location":{"type":"geo_point"},
       "remark_score":{"type":"double"},
       "price_per_man":{"type":"integer"},
       "category_id":{"type":"integer"},
       "category_name":{"type":"keyword"},
       "seller_id":{"type":"integer"},
       "seller_remark_score":{"type":"double"},
       "seller_disabled_flag":{"type":"integer"}
     }
   }
}
```
# search
```json
//GET /shop/_search
{
  "query": {
    "match": {"name": "凯悦"}
  },
  "_source": "*",
  "script_fields": {
    "distance": {
      "script": {
        "source": "haversin(lat,lon,doc['location'].lat,doc['location'].lon)",
        "lang": "expression",
        "params": {"lat": 31.2180870300,"lon": 121.4840937300}
      }
    }
  },
  "sort": [
    {
      "_geo_distance": {
        "location": {
          "lat": 31.2180870300,
          "lon": 121.4840937300
        },
        "order": "asc",
        "unit": "km",
        "distance_type": "arc"
      }
    }
  ]
}

```