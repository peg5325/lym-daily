<script setup lang="ts">
import type { Media } from '../types'

defineProps<{
  media: Media
}>()

const formatViews = (views?: number): string => {
  if (!views) return '0'
  if (views >= 10000) {
    return `${(views / 10000).toFixed(1)}만`
  }
  return views.toLocaleString()
}

const formatRelativeDate = (publishedAt: string): string => {
  const published = new Date(publishedAt)
  const today = new Date()

  // 시간 부분을 제거하고 날짜만 비교
  today.setHours(0, 0, 0, 0)
  published.setHours(0, 0, 0, 0)

  const diffTime = today.getTime() - published.getTime()
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))

  if (diffDays === 0) return '오늘'
  if (diffDays === 1) return '어제'
  return `${diffDays}일 전`
}
</script>

<template>
  <a
    :href="media.url"
    target="_blank"
    rel="noopener noreferrer"
    class="block bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow overflow-hidden"
  >
    <div class="relative aspect-video bg-gray-200">
      <img
        v-if="media.thumbnailUrl"
        :src="media.thumbnailUrl"
        :alt="media.title"
        class="w-full h-full object-cover"
      />

      <!-- NEW badge (오늘 올라온 영상) -->
      <div
        v-if="media.isNew"
        class="absolute top-2 right-2 bg-red-600 text-white text-xs font-bold px-2 py-1 rounded"
      >
        NEW!!
      </div>

      <!-- Play button overlay for videos -->
      <div
        v-if="media.type === 'VIDEO'"
        class="absolute inset-0 flex items-center justify-center pointer-events-none"
      >
        <div class="bg-red-600 bg-opacity-80 rounded-full p-3">
          <svg
            class="w-8 h-8 text-white"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path d="M6.3 2.841A1.5 1.5 0 004 4.11V15.89a1.5 1.5 0 002.3 1.269l9.344-5.89a1.5 1.5 0 000-2.538L6.3 2.84z" />
          </svg>
        </div>
      </div>
    </div>

    <div class="p-4">
      <h3 class="font-semibold text-gray-800 line-clamp-2 mb-2">
        {{ media.title }}
      </h3>

      <div class="flex items-center text-sm text-gray-600 gap-3">
        <span v-if="media.viewCount" class="flex items-center gap-1">
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
            <path
              fill-rule="evenodd"
              d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z"
              clip-rule="evenodd"
            />
          </svg>
          {{ formatViews(media.viewCount) }}
        </span>
        <span class="flex items-center gap-1">
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z"
              clip-rule="evenodd"
            />
          </svg>
          {{ formatRelativeDate(media.publishedAt) }}
        </span>
      </div>
    </div>
  </a>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
