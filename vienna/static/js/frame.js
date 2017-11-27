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
            str += '<li className="dropdown">';
            str += '<a data-toggle="dropdown" href="#">' + v.name + '</a>';
            str += '<ul className="dropdown-menu">';
            v.item.forEach(i => {
                str += '<li><a onClick="document.location.href = common.link(\'' + i.link + '\');">' + i.name + '</a></li>';
            });
            str += '</ul></li>';
        });
        $("#menu").html('<ul className="nav navbar-nav">' + str + '</ul>');
    });
});