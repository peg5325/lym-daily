<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NewsCard from '../components/NewsCard.vue'

// 뉴스 타입 정의 (Backend NewsDto와 맞춤)
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

// 상태 관리
const loading = ref(true)
const error = ref<string | null>(null)
const newsList = ref<News[]>([])
const todayDate = ref('')

// 날짜 포맷팅 함수
const formatDate = (date: Date): string => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekdays = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일']
  const weekday = weekdays[date.getDay()]

  return `${year}년 ${month}월 ${day}일 ${weekday}`
}

// 뉴스 데이터 가져오기
const fetchNews = async () => {
  try {
    loading.value = true
    error.value = null

    // Backend API 호출
    const response = await fetch('http://localhost:8080/api/news/collect')

    if (!response.ok) {
      throw new Error(`API 요청 실패: ${response.status}`)
    }

    const data = await response.json()
    newsList.value = data

  } catch (err) {
    error.value = '뉴스를 불러오는데 실패했습니다.'
    console.error('Error fetching news:', err)
  } finally {
    loading.value = false
  }
}

// 컴포넌트 마운트 시 실행
onMounted(() => {
  todayDate.value = formatDate(new Date())
  fetchNews()
})
</script>

<template>
  <div class="max-w-4xl mx-auto">
    <!-- 날짜 표시 -->
    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-800 text-center">
        {{ todayDate }}
      </h2>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <p class="mt-4 text-gray-600 text-lg">뉴스를 불러오는 중...</p>
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
      <p class="text-red-600 text-lg">{{ error }}</p>
      <button
        @click="fetchNews"
        class="mt-4 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
      >
        다시 시도
      </button>
    </div>

    <!-- 뉴스 목록 -->
    <div v-else class="space-y-4">
      <div v-if="newsList.length === 0" class="text-center py-12 bg-white rounded-lg shadow-md">
        <p class="text-gray-600 text-lg">오늘의 뉴스가 없습니다.</p>
      </div>

      <NewsCard
        v-for="(news, index) in newsList"
        :key="news.id"
        :news="news"
        :index="index + 1"
      />
    </div>
  </div>
</template>

<style scoped>
/* 추가 스타일은 Tailwind CSS 사용 */
</style>
