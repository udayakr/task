import { NavLink } from 'react-router-dom'
import { LayoutDashboard, FolderKanban, CheckSquare, User, Users } from 'lucide-react'
import { useAppSelector } from '@/hooks/useAppDispatch'
import { cn } from '@/utils/cn'

export function Sidebar() {
  const { sidebarOpen } = useAppSelector((s) => s.ui)
  const { user } = useAppSelector((s) => s.auth)

  if (!sidebarOpen) return null

  const navClass = ({ isActive }: { isActive: boolean }) =>
    cn('flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-colors',
      isActive ? 'bg-primary text-primary-foreground' : 'hover:bg-muted text-foreground')

  return (
    <aside className="w-56 border-r border-border bg-card flex flex-col gap-1 p-3">
      <NavLink to="/dashboard" className={navClass}>
        <LayoutDashboard className="w-4 h-4" /> Dashboard
      </NavLink>
      <NavLink to="/projects" className={navClass}>
        <FolderKanban className="w-4 h-4" /> Projects
      </NavLink>
      <NavLink to="/tasks/my-tasks" className={navClass}>
        <CheckSquare className="w-4 h-4" /> My Tasks
      </NavLink>
      <NavLink to="/profile" className={navClass}>
        <User className="w-4 h-4" /> Profile
      </NavLink>
      {user?.role === 'ADMIN' && (
        <NavLink to="/admin/users" className={navClass}>
          <Users className="w-4 h-4" /> User Management
        </NavLink>
      )}
    </aside>
  )
}
