var MF = {
    setNavigationBarTitle() {

    },
    navigateTo() {
        
    }
}

Proposal.host = {
    req(url, param, onSucc) {
        if (param != null)
            param["userKey"] = "1"
        common.req("app" + url, param, onSucc)
    }
}

var APP = {
    proposal: Proposal
}

var Main = React.createClass({
    getInitialState() {
        return {
            ages: [],
            applicant: {age: 25, gender: "M"},
            insurant: {age: 20, gender: "M"},
            index: 0,
            now: common.dateStr(new Date())
        }
    },
    componentDidMount() {
        let ages = []
        for (let i=0;i<70;i++)
            ages.push(i)
        this.setState({ ages: ages })

        MF.setNavigationBarTitle({ title: "建议书" })
        APP.proposal.create(this.state.applicant, this.state.insurant, (r) => {
            this.setState({ proposal: r, index: 0 }, this.onProposal)
        })
    },
    onProposal() {
        if (this.state.proposal.name)
            MF.setNavigationBarTitle({ title: this.state.proposal.name });
        if (this.state.proposal.detail.length > this.state.index) {
            APP.proposal.viewPlan(this.state.proposal.detail[this.state.index], (r) => {
                this.setState({ plan: r, insurant: r.insurant })
            })
        }
    },
    onShow() {
        let opt = APP.passport();
        if (opt) {
            if (opt.productId) {
                APP.proposal.addProduct(plan.planId, null, opt.productId, (r) => {
                    this.setState({ plan: r })
                })
            }
            if (opt.proposalId) {
                APP.proposal.load(opt.proposalId, (r) => {
                    this.setState({ proposal: r, index: 0 }, this.onProposal)
                })
            }
        }
    },
    onGenderChange(e) {
        this.state.insurant.gender = e.detail.value ? "F" : "M"
        this.refreshInsurant()
    },
    onAgeChange(e) {
        this.state.insurant.age = e.detail.value
        this.state.insurant.birthday = null
        this.refreshInsurant()
    },
    onBirthdayChange(e) {
        this.state.insurant.birthday = e.detail.value
        this.refreshInsurant()
    },
    onPlanSwitch(e) {
        this.setState({ index: e.currentTarget.dataset.i }, this.onProposal)
    },
    refreshInsurant() {
        APP.proposal.refreshInsurant(plan.planId, this.state.insurant, (r) => {
            this.setState({ insurant: r.insurant, plan: r })
        })
    },
    createPlan() {
        let insurant = { gender: "M", age: "20" }
        APP.proposal.createPlan(this.state.proposal.proposalId, insurant, (r) => {
            this.setState({ proposal: r, index: r.detail.length - 1 }, this.onProposal)
        })
    },
    addProduct() {
        MF.navigateTo({ url: './product_list' })
    },
    editProduct(e) {
        let win = this.selectComponent("#editor")
        win.pop(this.state.plan, e.currentTarget.dataset.i, () => {
            this.onProposal()
        })

        // APP.proposal.editProduct(plan.planId, e.currentTarget.dataset.i, r1 => {
        //   APP.proposal.listRiders(plan.planId, e.currentTarget.dataset.i, r2 => {
        //     let win = this.selectComponent("#editor")
        //     win.open(r1, r2)
        //   })
        // })
    },
    deleteProduct(e) {
        APP.proposal.deleteProduct(plan.planId, e.currentTarget.dataset.i, null, r => {
            this.setState({ plan: r })
        })
    },
    openProposalList() {
        MF.navigateTo({ url: './proposal_list' })
    },
    next() {
        MF.navigateTo({ url: './proposal_supply?proposalId=' + this.state.proposal.proposalId })
    },
    showBenefit() {
        MF.navigateTo({ url: './benefit?planId=' + plan.planId })
    },
    render() {
        let proposal = this.state.proposal
        let plan = this.state.plan
        return proposal == null || plan == null ? null : (
            <div>
                <div>
                    <div style={{display:"flex", position:"fixed", zIndex:"50", top:"0", backgroundColor:"#e6e6e6"}}>
                        { proposal.detail.map((v,i) =>
                            <div className={"tab " + (i == this.state.index ? 'tab-focus' : 'tab-blur')} key="i" style={{width:"250rpx"}} onClick={this.onPlanSwitch.bind(this, i)}>
                                <text className="text18">被保险人{i+1}</text>
                            </div>
                        )}
                        { proposal.detail.length < 3 ?
                            <div style={{width:750-250*proposal.detail.length+"rpx", height:"80rpx", textAlign:"right"}} onClick={this.createPlan}>
                                <image style={{width:"60rpx", height:"60rpx", padding:"10rpx", opacity: "0.7"}} src="./images/file-add.png"></image>
                            </div> : null
                        }
                    </div>
                    <div className="card-content" style={{marginTop:"80rpx"}}>
                        <div className="card-content-line">
                            <div className="card-content-label text17">性别</div>
                            <div className="card-content-widget text17">
                                <div className="text17" style={{padding:"0 10rpx 0 10rpx"}}>女</div>
                                <switch bindchange="onGenderChange" checked="{{insurant.gender == 'F'}}"/>
                                <div className="text17" style={{padding:"0 10rpx 0 10rpx"}}>男</div>
                            </div>
                        </div>
                    </div>
                    <div className="card-content">
                        <div className="card-content-line">
                            <div className="card-content-label text17">年龄</div>
                            <div className="card-content-widget">
                                <div style={{display:"flex"}}>
                                    <div className="text17">{this.state.insurant.age}周岁</div>
                                    <image style={{width:"60rpx", height:"60rpx", padding:"10rpx 0 10rpx 10rpx"}} src="./images/arrow-7-right.png"></image>
                                </div>
                                <image style={{width:"60rpx", height:"60rpx", padding:"10rpx 30rpx 10rpx 10rpx"}} src="./images/calendar.png"/>
                            </div>
                        </div>
                    </div>
                    <div className="card-content" style={{marginTop:"10rpx"}}>
                        { plan.product.map((v,i) =>
                            v.parent == null ?
                                <div className="product product-main text16" style={{marginTop:"10rpx"}} onClick={this.editProduct.bidn(this, i)}>
                                    <div style={{height:"70rpx", display:"flex"}}>
                                        <image style={{width:"60rpx", height:"60rpx", margin: "10rpx 10rpx 0 10rpx"}} src={plan.icons[v.vendor]}></image>
                                        <div style={{width:"600rpx", marginTop:"10rpx"}}>
                                            <text className="text20 eclipse">{v.name}</text>
                                        </div>
                                        <image style={{width:"50rpx", height:"50rpx", padding:"10rpx 10rpx 10rpx 10rpx", opacity:"0.4"}} src="./images/stop.png" onClick={this.deleteProduct.bind(this,i)}/>
                                    </div>
                                    <div style={{height:"60rpx", display:"flex"}}>
                                        <div className="left">
                                        </div>
                                        <div className="middle eclipse">
                                            <text>{v.purchase} / {v.insure} / {v.pay}</text>
                                        </div>
                                        <div className="right">
                                            <text style={{color:"#000"}}>{v.premium}元</text>
                                        </div>
                                    </div>
                                    <div style="height:10rpx;"></div>
                                </div> :
                                <div className="product product-rider text16">
                                    <div className="left">
                                        <text style="color:#0a0;">附</text>
                                    </div>
                                    <div className="middle eclipse">
                                        <text style={{color:"#000", marginRight:"10rpx"}}>{v.abbrName}</text>
                                        <text style={{color:"#aaa"}}>{v.purchase} / {v.insure} / {v.pay}</text>
                                    </div>
                                    <div className="right">
                                        <text style={{color:"#000"}}>{v.premium}元</text>
                                    </div>
                                </div>
                        )}
                        { plan.product && plan.product.length > 0 ?
                            <div className="card-content-line" style={{padding:"0 20rpx 0 20rpx", display:"block", marginTop:"10rpx", textAlign:"right", color:"#09bb07"}}>
                                <text className="text16">合计：{plan.premium}元</text>
                            </div> : null
                        }
                        <button style={{margin:"20rpx 10rpx 20rpx 10rpx", lineHeight:"80rpx", height:"80rpx", color:"#fff", backgroundColor:"#1aad19"}} className="text18" onClick={this.addProduct}>添加险种</button>
                    </div>
                </div>
                <div style={{height:"120rpx"}}></div>
                <div className="bottom">
                    <div className="btn-img" onClick={this.openProposalList}>
                        <image src="./images/documents.png"></image>
                        <text>建议书</text>
                    </div>
                    <div style={{width:"390rpx", padding:"6rpx 20rpx 6rpx 20rpx", lineHeight:"44rpx",margin:"0"}}>
                        <div>
                            <text className="text16" style={{color:"#fff"}}>首年保费：{plan.premium}元</text>
                        </div>
                        <div>
                            <text className="text16" style={{color:"#fff"}}>点击查看年度保费明细</text>
                        </div>
                    </div>
                    <div style={{width:"60rpx", height:"100rpx"}}>
                        <image style={{width:"60rpx", height:"60rpx", marginTop:"20rpx"}} src="./images/arrow-4-up.png"></image>
                    </div>
                    <div className="btn-img" onClick={this.showBenefit}>
                        <image src="./images/md-levels-alt.png"></image>
                        <text>利益</text>
                    </div>
                    <div className="btn-img" onClick={this.next}>
                        <image src="./images/arrow-1-right.png"></image>
                        <text>继续</text>
                    </div>
                </div>
            </div>
		)
    }
})


$(document).ready( function() {
	ReactDOM.render(
		<Main/>, document.getElementById("body")
	);
});