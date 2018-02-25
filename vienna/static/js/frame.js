FRAME = {};

FRAME.logout = function() {
    common.req("user/logout.json", null, function() {
        document.location.href = common.link("login.html");
    });
}

$(document).ready( function() {
    common.req("user/info.json", null, function(r) {
        var str = "";
        r.forEach(v => {
            str += '<li class="nav-item dropdown">';
            str += '<a class="nav-link dropdown-toggle" id="menu'+v.code+'" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">' + v.name + '</a>';
            str += '<div class="dropdown-menu" aria-labelledby="menu'+v.code+'">';
            v.item.forEach(i => {
                str += '<a class="dropdown-item" onclick="document.location.href = common.link(\'' + i.link + '\');">' + i.name + '</a>';
            });
            str += '</div></li>';
        });
        $("#menu").html('<ul class="navbar-nav mr-auto">' + str + '</ul>');
    }, function(r) {
        console.log(r);
    });
});