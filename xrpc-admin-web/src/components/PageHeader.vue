<template>
  <div class="page-header">
    <div>
      <h2>{{ title }}</h2>
      <p v-if="description" class="text-muted">{{ description }}</p>
    </div>
    <div><slot name="actions" /></div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string; description?: string }>()
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 18px; font-weight: 600; }
.page-header p { margin: 4px 0 0; }
</style>
