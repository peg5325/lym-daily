<script setup lang="ts">
import { onMounted, watch, ref } from 'vue'
import NewsCard from '../components/NewsCard.vue'
import MediaCard from '../components/MediaCard.vue'
import ScheduleCard from '../components/ScheduleCard.vue'
import { useDataFetch } from '../composables/useDataFetch'
import { useDateNavigation } from '../composables/useDateNavigation'
import type { DailySummary, Media, Schedule, MediaGroupResponse } from '../types'

// DataFetchAgent (Composable) 사용
const { data: summary, loading, error, fetchData } = useDataFetch<DailySummary>()
const { data: mediaGroup, loading: mediaLoading, fetchData: fetchMediaGroup } = useDataFetch<MediaGroupResponse>()
const { data: scheduleList, loading: scheduleLoading, fetchData: fetchSchedules } = useDataFetch<Schedule[]>()

// Shorts와 일반 영상 분리
const shortsList = ref<Media[]>([])
const regularVideosList = ref<Media[]>([])

// mediaGroup이 변경될 때마다 분리
watch(mediaGroup, (newGroup) => {
  if (newGroup) {
    shortsList.value = newGroup.shorts || []
    regularVideosList.value = newGroup.regularVideos || []
  }
})

// DateNavigationAgent (Composable) 사용
const {
  formattedDateISO,
  formattedDateKorean,
  isToday,
  goToPrevDay,
  goToNextDay,
  goToToday
} = useDateNavigation()

// API Base URL
// 프로덕션(Docker) 환경: 빈 문자열 - Nginx가 /api를 backend로 프록시
// 개발 환경: http://localhost:8080
const API_BASE_URL = import.meta.env.PROD ? '' : (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080')

// 날짜별 요약 가져오기
const fetchDailySummary = async () => {
  const endpoint = isToday.value
    ? `${API_BASE_URL}/api/today`
    : `${API_BASE_URL}/api/date/${formattedDateISO.value}`

  await fetchData(endpoint)
}

// 미디어 가져오기 (Shorts + 일반 영상)
const fetchTopMedia = async () => {
  await fetchMediaGroup(`${API_BASE_URL}/api/media/top3`)
}

// 이번 주 스케줄 가져오기
const fetchWeekSchedules = async () => {
  await fetchSchedules(`${API_BASE_URL}/api/schedules/week`)
}

// 컴포넌트 마운트 시 실행
onMounted(() => {
  fetchDailySummary()
  fetchTopMedia()
  fetchWeekSchedules()
})

// 날짜 변경 시 요약 다시 가져오기
watch(formattedDateISO, () => {
  fetchDailySummary()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-8">
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 날짜 네비게이션 -->
      <div class="bg-white rounded-lg shadow-md p-4 sm:p-6 mb-6 sticky top-0 z-10">
        <div class="flex items-center justify-between flex-wrap gap-3">
          <button
            @click="goToPrevDay"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition text-sm sm:text-base font-medium"
          >
            ◀ 어제
          </button>

          <div class="flex-1 text-center">
            <h2 class="text-xl sm:text-2xl md:text-3xl font-bold text-gray-800">
              {{ summary?.formattedDate || formattedDateKorean }}
            </h2>
            <button
              v-if="!isToday"
              @click="goToToday"
              class="mt-2 text-sm text-blue-600 hover:text-blue-700 underline"
            >
              오늘로 돌아가기
            </button>
          </div>

          <button
            @click="goToNextDay"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition text-sm sm:text-base font-medium"
          >
            내일 ▶
          </button>
        </div>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600 text-lg">불러오는 중...</p>
      </div>

      <!-- 에러 상태 -->
      <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-6 text-center mb-6">
        <p class="text-red-600 text-lg">{{ error }}</p>
        <button
          @click="fetchDailySummary"
          class="mt-4 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
        >
          다시 시도
        </button>
      </div>

      <!-- 메인 콘텐츠 -->
      <div v-else class="space-y-8">
        <!-- TOP 3 뉴스 섹션 -->
        <section>
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-2xl font-bold text-gray-800">📰 오늘의 뉴스</h3>
            <span v-if="summary" class="text-sm text-gray-600">
              총 {{ summary.totalNewsCount }}개 중 TOP 3
            </span>
          </div>

          <div v-if="!summary || summary.topNews.length === 0" class="text-center py-12 bg-white rounded-lg shadow-md">
            <p class="text-gray-600 text-lg">아직 뉴스가 없습니다.</p>
          </div>

          <div v-else class="space-y-4">
            <!-- AI 요약 안내 메시지 -->
            <div class="bg-blue-50 border border-blue-200 rounded-lg p-4">
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
        </section>

        <!-- Youtube 섹션 -->
        <section v-if="shortsList && shortsList.length > 0">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-2xl font-bold text-gray-800">📺 Youtube</h3>
            <a
              href="https://www.youtube.com/@LYW_official"
              target="_blank"
              rel="noopener noreferrer"
              class="text-sm text-blue-600 hover:text-blue-700 underline flex items-center gap-1"
            >
              임영웅 공식 유튜브 채널 바로가기
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
              </svg>
            </a>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <MediaCard
              v-for="media in shortsList"
              :key="media.id"
              :media="media"
            />
          </div>
        </section>

        <!-- 이번 주 스케줄 섹션 -->
        <section v-if="scheduleList && scheduleList.length > 0">
          <h3 class="text-2xl font-bold text-gray-800 mb-4">📅 이번 주 일정</h3>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <ScheduleCard
              v-for="schedule in scheduleList"
              :key="schedule.id"
              :schedule="schedule"
            />
          </div>
        </section>

        <!-- 스케줄이 없을 때 -->
        <section v-else-if="!scheduleLoading">
          <h3 class="text-2xl font-bold text-gray-800 mb-4">📅 이번 주 일정</h3>
          <div class="text-center py-8 bg-white rounded-lg shadow-md">
            <p class="text-gray-600">이번 주 예정된 일정이 없습니다.</p>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 추가 스타일은 Tailwind CSS 사용 */
</style>
