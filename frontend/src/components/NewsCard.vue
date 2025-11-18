<script setup lang="ts">
interface News {
  id: number
  title: string
  content?: string
  summary: string
  source?: string
  url: string
  thumbnailUrl?: string
  publishedAt: string
  importanceScore?: number
}

interface Props {
  news: News
  index: number
}

const props = defineProps<Props>()

// 날짜 포맷팅 함수 (LocalDate: YYYY-MM-DD 형식)
const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}월 ${day}일`
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
          {{ news.summary || news.content?.substring(0, 100) + '...' }}
        </p>

        <!-- 발행일 -->
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
              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
            />
          </svg>
          <span>{{ formatDate(news.publishedAt) }}</span>
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
