var Host = {
    req: function(url, param, onSucc) {
        if (param != null)
            param["userKey"] = "1"
        common.req("app" + url, param, onSucc)
    }
}

if (typeof Proposal != "undefined")
  Proposal.host = Host;