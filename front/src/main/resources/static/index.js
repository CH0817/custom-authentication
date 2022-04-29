let ssoToken = null;

$.ajaxSetup({
    contentType: 'application/json',
    // 允許請求帶有驗證訊息
    xhrFields: {withCredentials: true},
    beforeSend: function(request) {
        if(ssoToken) {
            request.setRequestHeader("SSO-TOKEN", ssoToken);
        }
    },
    error: function (jqXHR, textStatus, errorThrown) {
        if (jqXHR.status === 401) {
            ssoToken = null;
            window.location.replace(encodeURI('{cas url}'));
        } else {
            console.error(jqXHR);
            console.error(textStatus);
            console.error(errorThrown);
        }
    }
});

function getBackUserProfile() {
    $.ajax({
        url: 'http://localhost:8300/euisb/test/connect',
        type: 'post',
        cache: false,
        data: JSON.stringify({'username': 'rex'}),
        success: function (data, textStatus, jqXHR) {
            $('#backUserProfile1').append(JSON.stringify(data));
        }
    });
}

function getBack2UserProfile() {
    $.ajax({
        url: 'http://localhost:8300/euisbq/test/connect',
        type: 'post',
        cache: false,
        data: JSON.stringify({'username': 'rex'}),
        success: function (data, textStatus, jqXHR) {
            $('#backUserProfile2').append(JSON.stringify(data));
        }
    });
}

function redirectRequestEuisbq() {
    $.ajax({
        url: 'http://localhost:8300/euisbq/public/hello',
        type: 'post',
        contentType: "application/json",
        data: JSON.stringify({'name': 'rex'}),
        success: function (data, textStatus, jqXHR) {
            $('#public').append(JSON.stringify(data));
        }
    });
}

function checkCode() {
    let urlParams = new URLSearchParams(window.location.search);
    if(urlParams.has('code')) {
        ssoToken = urlParams.get('code');
        $.ajax({
            url: 'http://localhost:8300/euisbq/sso/token',
            type: 'post',
            cache: false,
            success:(data)=> {
                console.log('token is valid: ' + data);
            }
        });
    }
}

$(function(){
    checkCode();
});