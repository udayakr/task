import { Moon, Sun, LogOut, User, Menu } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useAppDispatch, useAppSelector } from '@/hooks/useAppDispatch'
import { logout } from '@/features/auth/authSlice'
import { toggleDarkMode, toggleSidebar } from '@/features/ui/uiSlice'
import { useLogoutMutation } from '@/api/authApi'

export function Navbar() {
  const dispatch = useAppDispatch()
  const navigate = useNavigate()
  const { user } = useAppSelector((s) => s.auth)
  const { darkMode } = useAppSelector((s) => s.ui)
  const [logoutApi] = useLogoutMutation()

  const handleLogout = async () => {
    const refreshToken = localStorage.getItem('refreshToken')
    try { await logoutApi({ refreshToken: refreshToken ?? undefined }).unwrap() } catch {}
    dispatch(logout())
    navigate('/login')
  }

  return (
    <header className="h-14 border-b border-border bg-card flex items-center px-4 gap-4">
      <button onClick={() => dispatch(toggleSidebar())} className="p-2 rounded hover:bg-muted" aria-label="Toggle sidebar">
        <Menu className="w-5 h-5" />
      </button>
      <span className="font-semibold text-lg flex-1">Task Manager</span>
      <button onClick={() => dispatch(toggleDarkMode())} className="p-2 rounded hover:bg-muted" aria-label="Toggle dark mode">
        {darkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
      </button>
      <button onClick={() => navigate('/profile')} className="p-2 rounded hover:bg-muted flex items-center gap-2" aria-label="Profile">
        <User className="w-5 h-5" />
        <span className="text-sm hidden md:block">{user?.firstName} {user?.lastName}</span>
      </button>
      <button onClick={handleLogout} className="p-2 rounded hover:bg-muted text-destructive" aria-label="Logout">
        <LogOut className="w-5 h-5" />
      </button>
    </header>
  )
}
