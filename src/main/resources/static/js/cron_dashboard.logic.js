let common_data = {}

axios.get('/groups/allGroups')
    .then(function (response) {
        common_data['all_groups'] = response.data.groups

        const MainDataApp = {
            data() {
                return {
                    all_groups: common_data['all_groups'].map(function gen(ele){return {"group": ele}})
                }
            }
        }
        Vue.createApp(MainDataApp).mount('#vue-app')
    })

