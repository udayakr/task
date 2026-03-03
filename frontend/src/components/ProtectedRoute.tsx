import { Navigate, Outlet } from 'react-router-dom'
import { useAppSelector } from '@/hooks/useAppDispatch'

interface ProtectedRouteProps {
  requiredRole?: 'ADMIN'
}

export function ProtectedRoute({ requiredRole }: ProtectedRouteProps) {
  const { isAuthenticated, user } = useAppSelector((s) => s.auth)

  if (!isAuthenticated) return <Navigate to="/login" replace />
  if (requiredRole && user?.role !== requiredRole) return <Navigate to="/dashboard" replace />

  return <Outlet />
}
