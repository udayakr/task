export type Role = 'ADMIN' | 'USER'
export type ProjectStatus = 'ACTIVE' | 'ARCHIVED'
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE'
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export interface User {
  id: string
  email: string
  firstName: string
  lastName: string
  role: Role
  active: boolean
  emailVerified: boolean
  createdAt: string
}

export interface Project {
  id: string
  name: string
  description?: string
  status: ProjectStatus
  owner: User
  memberCount: number
  createdAt: string
  updatedAt: string
}

export interface Task {
  id: string
  title: string
  description?: string
  status: TaskStatus
  priority: TaskPriority
  dueDate?: string
  projectId: string
  projectName: string
  assignee?: User
  createdBy: User
  tags?: string
  estimatedHours?: number
  actualHours?: number
  commentCount: number
  attachmentCount: number
  createdAt: string
  updatedAt: string
}

export interface Comment {
  id: string
  content: string
  author: User
  createdAt: string
  updatedAt: string
}

export interface Attachment {
  id: string
  fileName: string
  originalName: string
  fileSize: number
  contentType: string
  createdAt: string
}

export interface DashboardSummary {
  totalTasks: number
  completedTasks: number
  inProgressTasks: number
  overdueTasks: number
}

export interface ProjectStats {
  total: number
  todo: number
  inProgress: number
  review: number
  done: number
}

export interface ApiResponse<T> {
  data: T
  message: string
  timestamp: string
  error?: string
  details?: Record<string, string>
}

export interface PagedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: User
}
