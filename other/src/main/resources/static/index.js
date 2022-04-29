function redirectRequestEuisbq() {
    $.ajax({
        url: 'http://localhost:8300/euisbq/public/hello',
        type: 'post',
        contentType: "application/json",
        data: JSON.stringify({'name': 'rex'}),
        success: function (data, textStatus, jqXHR) {
            $('#response').append(JSON.stringify(data));
        }
    });
}

function requestEuisbq() {
    $.ajax({
        url: 'http://localhost:8877/other/test/hello',
        type: 'post',
        contentType: "application/json",
        data: JSON.stringify({'name': 'RexYu'}),
        success: function (data, textStatus, jqXHR) {
            $('#response').append(JSON.stringify(data));
        }
    });
}