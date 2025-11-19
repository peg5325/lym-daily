<script setup lang="ts">
import type { Schedule } from '../types'

defineProps<{
  schedule: Schedule
}>()

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  const month = date.getMonth() + 1
  const day = date.getDate()

  const weekdays = ['일', '월', '화', '수', '목', '금', '토']
  const weekday = weekdays[date.getDay()]

  return `${month}월 ${day}일 (${weekday})`
}
</script>

<template>
  <div class="bg-white rounded-lg shadow-md p-4 border-l-4 border-blue-500">
    <div class="flex items-start justify-between mb-2">
      <h3 class="font-semibold text-gray-800 text-lg">
        {{ schedule.title }}
      </h3>
      <span class="text-sm font-medium text-blue-600 whitespace-nowrap ml-2">
        {{ formatDate(schedule.eventDate) }}
      </span>
    </div>

    <div v-if="schedule.location" class="flex items-center text-sm text-gray-600 mb-1">
      <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
        <path
          fill-rule="evenodd"
          d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z"
          clip-rule="evenodd"
        />
      </svg>
      {{ schedule.location }}
    </div>

    <p v-if="schedule.description" class="text-sm text-gray-600 line-clamp-2">
      {{ schedule.description }}
    </p>
  </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
