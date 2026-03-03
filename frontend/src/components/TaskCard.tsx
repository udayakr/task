import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { Task, TaskPriority } from '@/types'
import { MessageSquare, Paperclip, Calendar } from 'lucide-react'
import { cn } from '@/utils/cn'
import { format } from 'date-fns'

const PRIORITY_COLORS: Record<TaskPriority, string> = {
  LOW: 'bg-slate-100 text-slate-700 dark:bg-slate-700 dark:text-slate-200',
  MEDIUM: 'bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-200',
  HIGH: 'bg-orange-100 text-orange-700 dark:bg-orange-900 dark:text-orange-200',
  CRITICAL: 'bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-200',
}

interface TaskCardProps {
  task: Task
  onClick: () => void
}

export function TaskCard({ task, onClick }: TaskCardProps) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id: task.id })

  const style = { transform: CSS.Transform.toString(transform), transition }
  const isOverdue = task.dueDate && new Date(task.dueDate) < new Date() && task.status !== 'DONE'

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      onClick={onClick}
      className={cn(
        'bg-card border border-border rounded-lg p-3 cursor-pointer shadow-sm hover:shadow-md transition-shadow',
        isDragging && 'opacity-50 rotate-1'
      )}
    >
      <div className="flex items-start justify-between gap-2 mb-2">
        <p className="text-sm font-medium leading-tight line-clamp-2">{task.title}</p>
        <span className={cn('text-xs px-2 py-0.5 rounded-full shrink-0', PRIORITY_COLORS[task.priority])}>
          {task.priority}
        </span>
      </div>
      <div className="flex items-center gap-3 text-xs text-muted-foreground mt-2">
        {task.dueDate && (
          <span className={cn('flex items-center gap-1', isOverdue && 'text-destructive')}>
            <Calendar className="w-3 h-3" />
            {format(new Date(task.dueDate), 'MMM d')}
          </span>
        )}
        {(task.commentCount ?? 0) > 0 && (
          <span className="flex items-center gap-1">
            <MessageSquare className="w-3 h-3" /> {task.commentCount}
          </span>
        )}
        {(task.attachmentCount ?? 0) > 0 && (
          <span className="flex items-center gap-1">
            <Paperclip className="w-3 h-3" /> {task.attachmentCount}
          </span>
        )}
        {task.assignee && (
          <span className="ml-auto flex items-center gap-1 bg-primary/10 text-primary rounded-full px-2 py-0.5">
            {task.assignee.firstName[0]}{task.assignee.lastName[0]}
          </span>
        )}
      </div>
    </div>
  )
}
