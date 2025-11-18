<script setup lang="ts">
import HomeView from './views/HomeView.vue'
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <header class="bg-hero-blue text-white py-6 shadow-lg">
      <div class="container mx-auto px-4">
        <h1 class="text-3xl md:text-4xl font-bold text-center">
          💙 임영웅 Daily 💙
        </h1>
        <p class="text-center text-blue-100 mt-2 text-sm md:text-base">
          오늘의 임영웅 소식
        </p>
      </div>
    </header>

    <!-- 메인 콘텐츠 -->
    <main class="container mx-auto px-4 py-6">
      <HomeView />
    </main>

    <!-- 푸터 -->
    <footer class="bg-white border-t border-gray-200 py-4 mt-8">
      <div class="container mx-auto px-4 text-center text-gray-600 text-sm">
        <p>Made with ❤️ for Mom</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
/* 추가 스타일은 Tailwind CSS 사용 */
</style>
