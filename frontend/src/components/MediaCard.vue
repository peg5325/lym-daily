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
