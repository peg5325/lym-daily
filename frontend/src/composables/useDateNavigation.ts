/**
 * DateNavigationAgent (Composable)
 *
 * 날짜 전환 및 한글 포맷팅 기능을 제공합니다.
 */

import { ref, computed, type Ref } from 'vue'

export interface UseDateNavigationOptions {
  initialDate?: Date
}

export function useDateNavigation(options: UseDateNavigationOptions = {}) {
  const currentDate: Ref<Date> = ref(options.initialDate || new Date())

  /**
   * 날짜를 YYYY-MM-DD 형식으로 포맷
   */
  const formattedDateISO = computed(() => {
    const year = currentDate.value.getFullYear()
    const month = String(currentDate.value.getMonth() + 1).padStart(2, '0')
    const day = String(currentDate.value.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  })

  /**
   * 날짜를 한글 형식으로 포맷
   * "2025년 1월 19일 일요일"
   */
  const formattedDateKorean = computed(() => {
    const year = currentDate.value.getFullYear()
    const month = currentDate.value.getMonth() + 1
    const day = currentDate.value.getDate()

    const weekdays = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일']
    const weekday = weekdays[currentDate.value.getDay()]

    return `${year}년 ${month}월 ${day}일 ${weekday}`
  })

  /**
   * 오늘인지 확인
   */
  const isToday = computed(() => {
    const today = new Date()
    return (
      currentDate.value.getFullYear() === today.getFullYear() &&
      currentDate.value.getMonth() === today.getMonth() &&
      currentDate.value.getDate() === today.getDate()
    )
  })

  /**
   * 어제로 이동
   */
  const goToPrevDay = () => {
    const prevDay = new Date(currentDate.value)
    prevDay.setDate(prevDay.getDate() - 1)
    currentDate.value = prevDay
  }

  /**
   * 내일로 이동
   */
  const goToNextDay = () => {
    const nextDay = new Date(currentDate.value)
    nextDay.setDate(nextDay.getDate() + 1)
    currentDate.value = nextDay
  }

  /**
   * 오늘로 이동
   */
  const goToToday = () => {
    currentDate.value = new Date()
  }

  /**
   * 특정 날짜로 이동 (ISO 문자열)
   */
  const goToDate = (dateString: string) => {
    const date = new Date(dateString)
    if (!isNaN(date.getTime())) {
      currentDate.value = date
    }
  }

  return {
    currentDate,
    formattedDateISO,
    formattedDateKorean,
    isToday,
    goToPrevDay,
    goToNextDay,
    goToToday,
    goToDate
  }
}
