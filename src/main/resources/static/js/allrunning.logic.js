let common_data = {}

function getAllTasks() {
    return common_data['all_running_tasks'].map(function gen(ele) {
        return {
            "start_time": ele["startTime"],
            "cluster": ele["cluster"],
            "group": ele["jobGroup"],
            "command": ele["jobCommand"].slice(0, 25) + " ...",
            "full_command": ele["jobCommand"],
            "type": ele["jobType"],
            "interval": ele["jobInterval"],
            "trigger": ele["jobTrigger"],
            "working_dir": ele["workingDir"],
            "log_hash": ele["logHash"],
            "task_status": ele["taskStatus"],
            "task_hash": ele["taskHash"]
        }
    })
}

function filterLogHash(hash){
    return getAllTasks().filter(it => it.log_hash === hash)[0];
}

function getAllCluster() {
    let tmp = [...new Set(common_data['all_running_tasks'].map(function (ele) {
        return ele['cluster']
    }))]

    let result = []

    for (let i = 0; i < tmp.length; i++) {
        if (i === 0) {
            result.push({"value": tmp[i], "tid": "#" + tmp[i], "active": 'active'})
        } else {
            result.push({"value": tmp[i], "tid": "#" + tmp[i], "active": ''})
        }
    }

    return result
}


const MainDataApp = {
    data() {
        return {
            all_clusters: getAllCluster(),
            all_tasks: getAllTasks(),
            m_start_time: " ",
            m_cluster: " ",
            m_group: " ",
            m_full_command: " ",
            m_type: " ",
            m_interval: " ",
            m_trigger: " ",
            m_working_dir: " ",
            m_log_hash: " ",
            m_task_status: " ",
            m_task_hash: " "
        }
    },

    methods: {
        filterByCluster(cluster) {
            return getAllTasks().filter(it => it.cluster === cluster)
        },
        update_detail_modal(log_hash) {
            let c = filterLogHash(log_hash);
            this.m_start_time = c["start_time"];
            this.m_cluster = c["cluster"];
            this.m_group = c["group"];
            this.m_full_command = c["full_command"];
            this.m_type = c["type"];
            this.m_interval = c["interval"];
            this.m_trigger = c["trigger"];
            this.m_working_dir = c["working_dir"];
            this.m_log_hash = c["log_hash"];
            this.m_task_status = c["task_status"];
            this.m_task_hash = c["task_hash"];
        },
        interrupt_task(th){
            axios.put('/tasks/interrupt?task_hash=' + th).then(
                function (){
                    alert("Interrupt request have been send.");
                }
            )
        },
        halt_group(th){
            axios.put('/tasks/halt?task_hash=' + th).then(
                function (){
                    alert("Halt request have been send.");
                }
            )
        }
    }
}

let app;
let root;

axios.get('/tasks/allRunning')
    .then(function (response) {
        common_data['all_running_tasks'] = response.data["tasks"]
    }).then(function () {
    app = Vue.createApp(MainDataApp);
    root = app.mount('#vue-app');
})



