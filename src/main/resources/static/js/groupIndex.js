function toggleBuyLike(buySuggestionId, buyLiked) {
    const likeBuyButton = document.getElementById('likeBuyButton');
    buyLiked = !buyLiked;
    if (buyLiked) {
        likeBuyButton.textContent = 'Unlike';
        likeBuyButton.classList.remove('btn-primary');
        likeBuyButton.classList.add('btn-danger');
    } else {
        likeBuyButton.textContent = 'Like';
        likeBuyButton.classList.remove('btn-danger');
        likeBuyButton.classList.add('btn-primary');
    }
    post_to_url("/suggestion/buyLike", {
        buySuggestionId: buySuggestionId,
        buyLiked: buyLiked
    }, "POST");
}

function toggleSellLike(sellSuggestionId, sellLiked) {
    const likeSellButton = document.getElementById('likeSellButton');
    sellLiked = !sellLiked;

    if (sellLiked) {
        likeSellButton.textContent = 'Unlike';
        likeSellButton.classList.remove('btn-primary');
        likeSellButton.classList.add('btn-danger');
    } else {
        likeSellButton.textContent = 'Like';
        likeSellButton.classList.remove('btn-danger');
        likeSellButton.classList.add('btn-primary');
    }
    post_to_url("/suggestion/sellLike", {
        sellSuggestionId: sellSuggestionId,
        sellLiked: sellLiked
    }, "POST");
}

function post_to_url(path, params, method) {
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for (var key in params) {
        if (params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
        }
    }

    document.body.appendChild(form);
    form.submit();
    form.reset();
}

// Comments::
function openCommentModal(suggestionId) {
    // Fetch comments for the specific suggestionId
    fetchComments(suggestionId)
        .then(comments => {
            console.log('After comments:', comments);

            // Display comments in the modal
            var commentDisplay = document.getElementById('commentDisplay');

            // Clear existing comments before displaying new ones
            commentDisplay.innerHTML = '';

            // Iterate over comments and create elements for each comment
            comments.forEach(function(comment) {
                // Create a new comment element
                var commentElement = document.createElement('div');
                commentElement.innerHTML = '<strong>' + comment.username + ': </strong> ' + comment.commentText;

                // Append the new comment to the display container
                commentDisplay.appendChild(commentElement);
            });
        })
        .catch(error => {
            // Handle errors here
            console.error('Error fetching comments:', error);
        });
}

function fetchComments(id) {
    const suggestionId = id;
    console.log(id);
    // Make a GET request to the endpoint and return the promise
    return fetch(`/suggestion/comment/${suggestionId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json(); // Assuming the response is in JSON format
        })
        .then(comments => {
            // Handle the received comments here
            console.log('Received comments:', comments);
            return comments;
        })
        .catch(error => {
            // Handle errors here
            console.error('Error fetching comments:', error);
            throw error; // Re-throw the error to propagate it to the calling code
        });
}

function addComment() {
    // Get the input value
    var commentInput = document.getElementById('commentInput').value;

    // Get the comment display container
    var commentDisplay = document.getElementById('commentDisplay');

    // Post the comment and handle the result
    postComment(commentInput)
        .then(comment => {
            // Create a new comment element
            var commentElement = document.createElement('div');
            commentElement.innerHTML = '<strong>' + comment.username + ':</strong> ' + comment.commentText;

            // Append the new comment to the display container
            commentDisplay.appendChild(commentElement);

            // Clear the input field
            document.getElementById('commentInput').value = '';
        })
        .catch(error => {
            // Handle errors here
            console.error('Error adding comment:', error);
        });
}

function postComment(text) {
    const suggestionId = document.getElementById('buySuggestionId').getAttribute('data-suggestionId');
    const commentText = text;
    console.log("text", commentText);

    // Make a POST request to the endpoint and return the promise
    return fetch('/suggestion/comment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `suggestionId=${suggestionId}&commentText=${commentText}`,
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json(); // Assuming the response is in JSON format
    })
    .then(comment => {
        // Handle the received comment here
        console.log('Received comment after adding a new comment:', comment);
        return comment;
    })
    .catch(error => {
        // Re-throw the error to propagate it to the calling code
        throw error;
    });
}
    function toggleTable(tableType) {
        if (tableType === 'buy') {
            document.getElementById('buySuggestionTable').style.display = 'block';
            document.getElementById('sellSuggestionTable').style.display = 'none';
        } else if (tableType === 'sell') {
            document.getElementById('buySuggestionTable').style.display = 'none';
            document.getElementById('sellSuggestionTable').style.display = 'block';
        }
    }
function searchSuggestions(tableId, searchInputId) {
            var input, filter, table, tr, td, i, txtValue;
            input = document.getElementById(searchInputId);
            filter = input.value.toUpperCase();
            table = document.getElementById(tableId);
            tr = table.getElementsByTagName("tr");

            for (i = 0; i < tr.length; i++) {
                td = tr[i].getElementsByTagName("td")[0]; // Change index based on the column you want to search
                if (td) {
                    txtValue = td.textContent || td.innerText;
                    if (txtValue.toUpperCase().indexOf(filter) > -1) {
                        tr[i].style.display = "";
                    } else {
                        tr[i].style.display = "none";
                    }
                }
            }
        }