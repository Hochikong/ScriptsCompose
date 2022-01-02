let common_data = {}

function getAllTasks() {
    return common_data['all_tasks'].map(function gen(ele) {
        return {
            "cluster": ele["cluster"],
            "group": ele["group_name"],
            "command": ele["command"].slice(0, 25) + " ...",
            "full_command": ele["command"],
            "type": ele["job_type"],
            "interval": ele["interval"],
            "trigger": ele["start_at"],
            "task_hash": ele["task_hash"]
        }
    })
}

function getAllCluster() {
    let tmp = [...new Set(common_data['all_tasks'].map(function (ele) {
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
        }
    },

    methods: {
        filterByCluster(cluster) {
            return getAllTasks().filter(it => it.cluster === cluster)
        }
    }
}

let app;
let root;

axios.get('/tasks/allTasks/byType/v2?type=one')
    .then(function (response) {
        common_data['all_tasks'] = response.data["tasks"]
    }).then(function () {
    app = Vue.createApp(MainDataApp);
    root = app.mount('#vue-app');
})



