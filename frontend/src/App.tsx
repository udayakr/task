import { Routes, Route, Navigate } from 'react-router-dom'
import { useEffect } from 'react'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { Navbar } from '@/components/Navbar'
import { Sidebar } from '@/components/Sidebar'
import { LoginPage } from '@/pages/LoginPage'
import { RegisterPage } from '@/pages/RegisterPage'
import { DashboardPage } from '@/pages/DashboardPage'
import { ProjectsPage } from '@/pages/ProjectsPage'
import { ProjectDetailPage } from '@/pages/ProjectDetailPage'
import { MyTasksPage } from '@/pages/MyTasksPage'
import { ProfilePage } from '@/pages/ProfilePage'
import { AdminUsersPage } from '@/pages/AdminUsersPage'
import { ForgotPasswordPage } from '@/pages/ForgotPasswordPage'
import { useAppSelector } from '@/hooks/useAppDispatch'

function AppLayout() {
  return (
    <div className="h-screen flex flex-col bg-background text-foreground">
      <Navbar />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-auto">
          <Routes>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/projects" element={<ProjectsPage />} />
            <Route path="/projects/:id" element={<ProjectDetailPage />} />
            <Route path="/tasks/my-tasks" element={<MyTasksPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route element={<ProtectedRoute requiredRole="ADMIN" />}>
              <Route path="/admin/users" element={<AdminUsersPage />} />
            </Route>
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </main>
      </div>
    </div>
  )
}

function App() {
  const { darkMode } = useAppSelector((s) => s.ui)

  useEffect(() => {
    document.documentElement.classList.toggle('dark', darkMode)
  }, [darkMode])

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/*" element={<AppLayout />} />
      </Route>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}

export default App
