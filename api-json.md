## REST API Controllers

#### UtilsController

> Load fake data via controller (only on profiles "dev" and "docker")

    # Request 
        GET /utils/load-fake-data
        
    # Response
        {
            "code": "OK",
            "errors": null,
            "warnings": null
        }


#### OperatorController    
> Get all Operators

    # Request
        GET|POST /operator/all?page=3&size=2
        GET|POST /operator/all
    
    # Response
        {
            "code": "OK",
            "errors": null,
            "warnings": null,
            "result": [
                {
                    "id": 92,
                    "name": "Operator #12",
                    "owner": 36,
                    ...
                },
                ...
            ]
        }


> Get all Operators groupped by Owner or Type

    # Request
        GET|POST /operator/all-grouped?groupBy=owner
    
    # Response
        {
            "code": "OK",
            "errors": null,
            "warnings": null,
            "result": {
                "owner-slug-3": [
                    {
                        "id": 109,
                        "name": "Operator #5",
                        "owner": 34,
                        ...
                    }
                ]
                ...
            }
        }

> Get all Operators groupped by Type

    # Request
        GET|POST /operator/all-grouped?groupBy=typename

    # Response
        {
            "code": "OK",
            "errors": null,
            "warnings": null,
            "result": {
                "private": [
                    {
                        "id": 109,
                        "name": "Operator #5",
                        "owner": 34,
                        ...
                    }
                ]
                ...
            }
        }


> Get all operators matching required types and owners, groupped by type and owner

    # Request
        POST /operator/by-vehicle-owner
        {"vehicleTypes":["train","underground","dlr","overground"],"ownerSlugs":["owner-slug-6","owner-slug-3"]}}
    
    # Response
        {
            "code": "OK",
            "errors": null,
            "warnings": null,
            "result": {
                "Underground": {
                    "owner-slug-3": [
                        {
                            "id": 87,
                            "name": "Operator #11",
                            "owner": 40,
                            ...
                        }
                    ]
                    ...
                }
            }
        }



> Get list of operator IDs by type

    # Request
        GET /operator/id-by-type
    
    # Response
        {
            "code": "OK",
            "errors": null,
            "warnings": null,
            "result": {
                "private": [
                    104,
                    91
                ]
            }
        }


> Add/update operator

    # Request
        PUT /operator/add
        {"name":"Operator Ad 1","owner":6,"operatorCode":"operator-ad-1","licenceId":5019,"status":1,"ownerSlug":"owner-slug-15","type":1}
    
    # Response
        {
            "code": "OK",
            "errors": null,
            "warnings": null,
            "result": [
                {
                    "id": 306,
                    "name": "Operator Ad 1",
                    "owner": 6,
                    ...
                }
            ]
        }

> Update / patch
    
    # Request
        PATCH /operator/update/306
        {"name":"Operator Ad 1ab","owner":6,"operatorCode":"operator-changed-1","licenceId":5019,"status":1,"ownerSlug":"owner-slug-15","type":1}
    
    # Response
        {
            "code": "OK",
            "errors": null,
            "warnings": null,
            "result": [
                {
                    "id": 306,
                    "name": "Operator Ad 1ab",
                    "owner": 6,
                    ...
                }
            ]
        }


> Other services

    # Requests
        GET /operator/get/104
        HEAD /operator/get/104
        DELETE /operator/delete/104
        OPTIONS /operator/options

#### ApiController 

**API Responses**

Response OK*

    {
        "passengerId": 106,
        "balance": 300,
        "message": "",
        "status": "OK",
        "errorCode": ""
    }
    
Response ERROR*
    
    {
        "passengerId": 0,
        "balance": 0,
        "message": "licenceId should not be empty",
        "status": "ERROR",
        "errorCode": "GENERAL"
    }
    
**API Requests**

POST */api/init*

    {
        "journeyIdentifer":"journey-identif-1",
        "licenceId":5004,
        "passengerId":"106",
        "pointId":4,
        "actionIdentifier":"action-identif-1"
    }


POST */api/balance*

    {"licenceId":5004,"passengerId":"106"}


POST */api/touchin*

POST */api/touchout*

POST */api/refund*

*(action Refund must have  exactly the same data as TOUCHIN request, especially actionIdentifier and journeylegIdentifer)*
        
    {
        "journeyIdentifer":"journey-identif-1",
        "licenceId":5004,
        "passengerId":"106",
        "pointId":4,
        "actionIdentifier":"action-identif-act-2",
        "journeylegIdentifer":"journey-identif-jleg-2"
    }

---