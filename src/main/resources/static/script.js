// Load history data
function loadHistory() {
    fetchHistoryData().then(data => {
        const container = document.getElementById('history-container');
        container.innerHTML = ''; // Clear the container before adding new buttons

        data.forEach(button => {
            const historyBtn = document.createElement('button');
            historyBtn.classList.add('mybutton');
            historyBtn.textContent = button.label;
            historyBtn.id = button.id;
            historyBtn.onclick = () => {
                fetchAnalysisById(historyBtn.id)
                    .then(analysis => {
                        loadAnalysis(analysis); // Use the analysis data to load it into your page
                    })
                    .catch(error => {
                        console.error('Error while fetching analysis data: ', error);
                    });
            };
            container.appendChild(historyBtn);
        });
    }).catch(error => {
        console.error('Error while loading history data: ', error);
    });
}

// Fetch history data by API request
function fetchHistoryData() {
    return fetch('http://localhost:8080/api/analysis/history')
        .then(response => response.json())
        .then(data => {
            return data;
        })
        .catch(error => {
            console.error('Error while trying to fetch the history data: ', error);
            throw error; // Propagate the error to be handled in the loadHistory function
        });
}


var barColors = [
    "red", "green", "blue", "orange", "brown", "purple", "pink", "yellow", "cyan",
    "magenta", "teal", "gray", "indigo", "lime", "violet",
    "turquoise", "salmon", "peach", "chartreuse", "lavender", "beige", "crimson",
    "ivory", "maroon", "goldenrod", "coral", "plum"
]; // Colors for the charts
var lettersX = [], lettersY=[]; // The first graph
var wordsX = [], wordsY = []; // The second graph

function loadAnalysis(data){
    const lettersAppearances = data.lettersAppearances;
    const wordsAppearances = data.wordsAppearances;
    const wordsWithRepeatedLetters = data.wordsWithRepeatedLetters;

    if (lettersAppearances) {
        // Delete the previous chart data before adding new data
        lettersX.length = 0;
        lettersY.length = 0;

        // Running on the map in order to create the X-values(lettersX) array and Y-values array (lettersY).
        Object.keys(lettersAppearances).forEach(letter => {
            const count = lettersAppearances[letter];
            lettersX.push(letter);
            lettersY.push(count);
            });
    }

    if (wordsAppearances) {
        wordsX.length = 0;
        wordsY.length = 0;

        // Running on the map in order to create the X-values(wordsX) array and Y-values array (wordsY).
        Object.keys(wordsAppearances).forEach(word => {
            const count = wordsAppearances[word];
            wordsX.push(word);
            wordsY.push(count);
            });
    }

    const wordsWithRepeatedLettersLabel = document.getElementById('analyzeResult3');

    if (wordsWithRepeatedLetters != null) {
        wordsWithRepeatedLettersLabel.innerHTML = 'מילים המכילות אותיות כפולות' + '<br>' + wordsWithRepeatedLetters;
    }

    updateCharts();
    loadHistory();
}

// Restore the analysis object from the db, by making an HTTP request
function fetchAnalysisById(id) {
    fetch('http://localhost:8080/api/analysis?id=' + id, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        loadAnalysis(data);
    })
    .catch(error => {
        console.error('Error while trying to restore the analysis object: ', error);
    });
}

sendBtn.onclick = () => {
    const inputData = strinput.value; // Get the text input value

    // Input validation
    if (inputData == ""){
        alert('לא הוזן קלט');
        return;
    }

    const hebrewRegex = /^[\u0590-\u05FF\s,.?]*$/;

    // Check if the input matching to the regex
    if (!hebrewRegex.test(inputData)) {
        alert("הקלט יכול להכיל רק תווים בעברית, וכן את הסימנים: פסיק, נקודה, סימן שאלה");
        return;
    }

    const formData = new URLSearchParams();
    formData.append('st', inputData);

    fetch('http://localhost:8080/api/analysis/analyze', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
        loadAnalysis(data)

    })
    .catch(error => {
        console.error('Error:', error);
    });
};


let chart1, chart2; // Variables to store the instances of the charts

function updateCharts() {
    // Destroy the previous chart if exists
    if (chart1) {
        chart1.destroy();
    }
    if (chart2) {
        chart2.destroy();
    }

    // Create the new chart for lettersAppearances (the first graph)
    chart1 = new Chart("myChart1", {
        type: "pie",
        data: {
            labels: lettersX,
            datasets: [{
                backgroundColor: barColors,
                data: lettersY
            }]
        },
        options: {
            title: {
                display: true,
                text: "פילוח מס' ההופעות של אות בקלט"
            }
        }
    });

    // Create the chart for wordsAppearances (the second graph)
    chart2 = new Chart("myChart2", {
        type: "pie",
        data: {
            labels: wordsX,
            datasets: [{
                backgroundColor: barColors,
                data: wordsY
            }]
        },
        options: {
            title: {
                display: true,
                text: "פילוח מס' ההופעות של מילה בקלט"
            }
        }
    });
}

window.onload = function() {
    loadHistory(); // Fetch and load the history when the window is loading.
};