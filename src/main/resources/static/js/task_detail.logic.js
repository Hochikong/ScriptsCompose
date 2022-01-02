let common_data = {}

function getTaskDetail() {
    return {
        "cluster": common_data['task']["cluster"],
        "group": common_data['task']["group_name"],
        "command": common_data['task']["command"],
        "type": common_data['task']["job_type"],
        "interval": common_data['task']["interval"],
        "trigger": common_data['task']["start_at"]
    }
}

function get_all_current_briefs() {
    return common_data['brief'];
}

// function replaceT(){
//     for (let b in common_data['brief']) {
//         b['duration'] = b['duration'].replace("T", " ")
//     }
// }

function get_log_content(target_log_hash) {
    axios.get('/logs/detail?log_hash=' + target_log_hash)
        .then(function (response) {
            common_data['log'] = response.data['log'];
        }).then(function () {
        root.set_log_content();
    })
}

function get_new_briefs(task_hash) {
    axios.get('logs/brief?task_hash=' + task_hash + "&st=" + common_data['st_timestamp'] + "&ed=" + common_data['ed_timestamp'])
        .then(function (response) {
            common_data['brief'] = response.data['brief'];
        }).then(function () {
        root.$data.show_all_briefs = get_all_current_briefs();
    })
}

function getLogHash() {
    if (common_data['brief'].length > 0) {
        return common_data['brief'][0]['log_hash'];
    } else {
        return " ";
    }
}

const MainDataApp = {
    data() {
        return {
            current_detail: getTaskDetail(),
            get_time_range: "10m",
            brief_duration: "Empty",
            target_log_hash: getLogHash(),
            show_all_briefs: get_all_current_briefs(),
            log_content: " ",
            st_str: "",
            ed_str: ""
        }
    },
    methods: {
        switch_day_range(range) {
            this.st_str = "";
            this.ed_str = "";
            this.get_time_range = range;
            if (range.endsWith("m")) {
                let MS_PER_MINUTE = 60000;
                let delta = parseInt(range.replace("m", ""));
                common_data["ed_timestamp"] = new Date().getTime();
                common_data["st_timestamp"] = new Date(new Date() - delta * MS_PER_MINUTE).getTime();
            } else if (range.endsWith("h")) {
                let MS_PER_HOUR = 3600000;
                let delta = parseInt(range.replace("h", ""));
                common_data["ed_timestamp"] = new Date().getTime();
                common_data["st_timestamp"] = new Date(new Date() - delta * MS_PER_HOUR).getTime();
            } else if (range.endsWith("d")) {
                let MS_PER_DAY = 86400000;
                let delta = parseInt(range.replace("d", ""));
                common_data["ed_timestamp"] = new Date().getTime();
                common_data["st_timestamp"] = new Date(new Date() - delta * MS_PER_DAY).getTime();
            }
        },
        reset_day_range() {
            this.get_time_range = "By Input";
        },
        fetch_brief() {
            if (this.st_str.length > 0) {
                common_data["st_timestamp"] = Date.parse(this.st_str)
            }
            if (this.ed_str.length > 0) {
                common_data["ed_timestamp"] = Date.parse(this.ed_str)
            }
            get_new_briefs(current_task_hash);
        },
        get_log_content() {
            get_log_content(this.target_log_hash);
        },
        set_log_content() {
            this.log_content = common_data['log'];
        },
        change_brief(nd, lh) {
            this.brief_duration = nd;
            this.target_log_hash = lh;
        }
    }
}

axios.get('/tasks/detail?task_hash=' + current_task_hash)
    .then(function (response) {
        common_data['task'] = response.data["task"];
    })

let app;
let root;

// default fetch in 2 mintues
axios.get('logs/brief?task_hash=' + current_task_hash + "&st=" + new Date(new Date() - 30 * 60000).getTime() + "&ed=" + new Date().getTime())
    .then(function (response) {
        common_data['brief'] = response.data['brief'];
    }).then(function () {
    app = Vue.createApp(MainDataApp);
    root = app.mount('#vue-app');
})






