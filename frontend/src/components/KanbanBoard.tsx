import { useState } from 'react'
import { DndContext, DragEndEvent, DragOverEvent, PointerSensor, useSensor, useSensors } from '@dnd-kit/core'
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable'
import { Task, TaskStatus } from '@/types'
import { useUpdateTaskStatusMutation } from '@/api/taskApi'
import { TaskCard } from './TaskCard'

const COLUMNS: { id: TaskStatus; label: string }[] = [
  { id: 'TODO', label: 'To Do' },
  { id: 'IN_PROGRESS', label: 'In Progress' },
  { id: 'REVIEW', label: 'Review' },
  { id: 'DONE', label: 'Done' },
]

interface KanbanBoardProps {
  tasks: Task[]
  projectId: string
  onTaskClick: (task: Task) => void
}

export function KanbanBoard({ tasks, projectId, onTaskClick }: KanbanBoardProps) {
  const [updateStatus] = useUpdateTaskStatusMutation()
  const [optimisticTasks, setOptimisticTasks] = useState<Task[]>(tasks)

  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 8 } }))

  const tasksByStatus = (status: TaskStatus) =>
    optimisticTasks.filter((t) => t.status === status)

  const handleDragEnd = async (event: DragEndEvent) => {
    const { active, over } = event
    if (!over) return
    const taskId = active.id as string
    const newStatus = over.id as TaskStatus
    const task = optimisticTasks.find((t) => t.id === taskId)
    if (!task || task.status === newStatus) return

    setOptimisticTasks((prev) => prev.map((t) => t.id === taskId ? { ...t, status: newStatus } : t))
    try {
      await updateStatus({ projectId, taskId, status: newStatus }).unwrap()
    } catch {
      setOptimisticTasks(tasks)
    }
  }

  const handleDragOver = (event: DragOverEvent) => {
    const { over } = event
    if (over && COLUMNS.some((c) => c.id === over.id)) {
      const taskId = event.active.id as string
      setOptimisticTasks((prev) =>
        prev.map((t) => t.id === taskId ? { ...t, status: over.id as TaskStatus } : t))
    }
  }

  return (
    <DndContext sensors={sensors} onDragEnd={handleDragEnd} onDragOver={handleDragOver}>
      <div className="grid grid-cols-4 gap-4 h-full overflow-x-auto">
        {COLUMNS.map((col) => (
          <div key={col.id} id={col.id} className="flex flex-col gap-2 min-h-64">
            <div className="flex items-center justify-between px-2 py-1">
              <h3 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">{col.label}</h3>
              <span className="text-xs bg-muted px-2 py-0.5 rounded-full">{tasksByStatus(col.id).length}</span>
            </div>
            <SortableContext items={tasksByStatus(col.id).map((t) => t.id)} strategy={verticalListSortingStrategy}>
              <div className="flex flex-col gap-2 flex-1 bg-muted/30 rounded-lg p-2 min-h-32">
                {tasksByStatus(col.id).map((task) => (
                  <TaskCard key={task.id} task={task} onClick={() => onTaskClick(task)} />
                ))}
              </div>
            </SortableContext>
          </div>
        ))}
      </div>
    </DndContext>
  )
}
