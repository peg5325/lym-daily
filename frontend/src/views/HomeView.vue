<script setup lang="ts">
import { onMounted } from 'vue'
import NewsCard from '../components/NewsCard.vue'
import { useDataFetch } from '../composables/useDataFetch'
import type { DailySummary } from '../types'

// DataFetchAgent (Composable) 사용
const { data: summary, loading, error, fetchData } = useDataFetch<DailySummary>()

// 오늘의 요약 가져오기
const fetchTodaySummary = async () => {
  await fetchData('http://localhost:8080/api/today')
}

// 컴포넌트 마운트 시 실행
onMounted(() => {
  fetchTodaySummary()
})
</script>

<template>
  <div class="max-w-4xl mx-auto">
    <!-- 날짜 표시 -->
    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-800 text-center">
        {{ summary?.formattedDate || '로딩 중...' }}
      </h2>
      <p v-if="summary" class="text-center text-gray-500 mt-2 text-sm">
        총 {{ summary.totalNewsCount }}개의 뉴스 중 TOP 3
      </p>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <p class="mt-4 text-gray-600 text-lg">오늘의 요약을 불러오는 중...</p>
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
      <p class="text-red-600 text-lg">{{ error }}</p>
      <button
        @click="fetchTodaySummary"
        class="mt-4 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
      >
        다시 시도
      </button>
    </div>

    <!-- TOP 3 뉴스 목록 -->
    <div v-else class="space-y-4">
      <div v-if="!summary || summary.topNews.length === 0" class="text-center py-12 bg-white rounded-lg shadow-md">
        <p class="text-gray-600 text-lg">오늘의 뉴스가 없습니다.</p>
        <p class="text-gray-500 text-sm mt-2">
          아직 뉴스가 수집되지 않았습니다. 관리자 API를 통해 수동으로 수집할 수 있습니다.
        </p>
      </div>

      <div v-else>
        <!-- AI 요약 안내 메시지 -->
        <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
          <div class="flex items-center">
            <svg class="w-5 h-5 text-blue-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
            </svg>
            <p class="text-blue-700 text-sm font-medium">
              AI가 선정한 오늘의 중요한 소식입니다
            </p>
          </div>
        </div>

        <!-- TOP 3 뉴스 카드 -->
        <NewsCard
          v-for="(news, index) in summary.topNews"
          :key="news.id"
          :news="news"
          :index="index + 1"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 추가 스타일은 Tailwind CSS 사용 */
</style>
