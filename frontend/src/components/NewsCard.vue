<script setup lang="ts">
interface News {
  id: number
  title: string
  summary: string
  url: string
  publishedAt: string
}

interface Props {
  news: News
  index: number
}

const props = defineProps<Props>()

// 시간 포맷팅 함수
const formatTime = (dateString: string): string => {
  const date = new Date(dateString)
  const hours = date.getHours()
  const minutes = date.getMinutes()
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`
}

// 외부 링크 열기
const openLink = (url: string) => {
  window.open(url, '_blank')
}
</script>

<template>
  <div
    class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 p-6 cursor-pointer"
    @click="openLink(news.url)"
  >
    <!-- 순번 배지 -->
    <div class="flex items-start gap-4">
      <div class="flex-shrink-0">
        <span
          class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-blue-600 text-white font-bold text-lg"
        >
          {{ index }}
        </span>
      </div>

      <!-- 뉴스 내용 -->
      <div class="flex-1 min-w-0">
        <!-- 제목 -->
        <h3 class="text-xl md:text-2xl font-bold text-gray-900 mb-2 leading-tight">
          {{ news.title }}
        </h3>

        <!-- 요약 -->
        <p class="text-base md:text-lg text-gray-700 mb-3 leading-relaxed">
          {{ news.summary }}
        </p>

        <!-- 시간 -->
        <div class="flex items-center text-sm text-gray-500">
          <svg
            class="w-4 h-4 mr-1"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <span>{{ formatTime(news.publishedAt) }}</span>
        </div>
      </div>

      <!-- 화살표 아이콘 -->
      <div class="flex-shrink-0">
        <svg
          class="w-6 h-6 text-gray-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M9 5l7 7-7 7"
          />
        </svg>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 추가 스타일은 Tailwind CSS 사용 */
</style>
