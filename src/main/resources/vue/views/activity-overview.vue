<template id="activity-overview">
  <app-layout>
    <div>
      <div>
        <ul class="activity-overview-list">
          <li v-for="activities in activities">
            <a :href="`/users/${activities.id}`">{{activities.name}} </a>
          </li>
        </ul>
      </div>
    </div>
  </app-layout>
</template>
<script>
Vue.component("activity-overview", {
  template: `#activity-overview`,
  data: () => ({
    activities: [],
  }),
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers: function () {
      axios.get("/api/activities")
          .then(res => this.activities = res.data)
          .catch(() => alert("Error while fetching users"));
    }
  }
});
</script>