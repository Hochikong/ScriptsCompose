let common_data = {}

const MainDataApp = {
    data() {
        return {
            all_groups: common_data['all_tasks'].map(function gen(ele) {
                return {"group": ele["group_name"]}
            }),
            all_tasks: common_data['all_tasks'].map(function gen(ele) {
                return {
                    "command": ele["command"].slice(0, 15) + " ...",
                    "type": ele["job_type"],
                    "interval": ele["interval"],
                    "trigger": ele["start_at"]
                }
            })
        }
    }
}

axios.get('/tasks/allTasks/byType?type=cron')
    .then(function (response) {
        common_data['all_tasks'] = response.data["tasks"]
    }).then(function () {
    Vue.createApp(MainDataApp).mount('#vue-app')
})



