<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NewsCard from '../components/NewsCard.vue'

// 뉴스 타입 정의
interface News {
  id: number
  title: string
  summary: string
  url: string
  publishedAt: string
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

    // TODO: 실제 API 호출로 교체
    // const response = await fetch('http://localhost:8080/api/news/today')
    // const data = await response.json()
    // newsList.value = data

    // 임시 더미 데이터
    await new Promise(resolve => setTimeout(resolve, 500))
    newsList.value = [
      {
        id: 1,
        title: '임영웅, 새 앨범 발매 예정',
        summary: '가수 임영웅이 새 앨범을 준비 중이라는 소식이 전해졌다.',
        url: 'https://naver.com',
        publishedAt: '2024-01-15T10:00:00'
      },
      {
        id: 2,
        title: '임영웅 콘서트 티켓 오픈',
        summary: '2024년 전국 투어 콘서트 티켓이 다음 주 오픈된다.',
        url: 'https://naver.com',
        publishedAt: '2024-01-15T09:30:00'
      },
      {
        id: 3,
        title: '임영웅, 음원차트 1위 달성',
        summary: '최신 싱글이 발매 첫 주 주요 음원차트 1위를 기록했다.',
        url: 'https://naver.com',
        publishedAt: '2024-01-15T08:00:00'
      }
    ]
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
