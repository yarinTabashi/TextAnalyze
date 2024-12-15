

 # Text-Analyze
The goal of this project is to perform analyses on a given text (Hebrew text containing and the punctuation marks: .,?).


Users have the option to process a new text or access the last 15 analyses that were performed.


## API Reference

#### Get specific anaylysis from the db

```http
  GET /api/analysis
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `string` | **Required**. The ID of the analysis to retrieve. |

#### Get all history records
```http
    GET /api/analysis/history
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `None`      | `---` | **Required**. Retrieves the last 15 analysis records, that contains just the string and its id. |

* In this way, it keeps the history records lightweight (contatining only the string and its ID), and allows users to retrieve the full analysis details by selecting a specific record when needed.

#### Analyze Input Text

```http
    POST /api/analysis/analyze
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `st`      | `String` | **Required**. A text to analyze, containing only Hebrew characters and the punctuation marks: (.)(?)(,). The length is limited to 100 characters.|

* First, it will try to retrieve it from the db (for case taht the user already search for it recently, but didn't choose it from the history).

## Stack
* **Backend:**  Java (spring boot framework)
* **Database:** MongoDB (initlized as a service with docker-compose)
* **Frontend:** JS, HTML, css
## Analysis Result
Each analysis result contains this properties:
- How many times each letter is used (Map<Char, Integer> and displayed as a pie chart)
- How many times each word is used (Map<String, Integer> and displayed as a pie chart)
- Amount of words that includes in the word the same letter more than once (Integer value)
## Project Setup

1. **Clone the repository**
```bash
git clone https://github.com/yarintabashi/TextAnalyze.git
cd TextAnalyze
```
2. **Build the Spring Boot application**
```bash
mvn clean install
```

3. **Start the application with Docker-Compose**
```bash
docker-compose up --build
```

4. **Access the Web Application**
```bash
  http://localhost:8080
```
