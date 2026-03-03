import { useState } from 'react'
import { useParams } from 'react-router-dom'
import { useGetProjectQuery } from '@/api/projectApi'
import { useGetTasksQuery, useCreateTaskMutation } from '@/api/taskApi'
import { KanbanBoard } from '@/components/KanbanBoard'
import { CardSkeleton } from '@/components/LoadingSkeleton'
import { Task } from '@/types'
import { Plus, X } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'

const schema = z.object({ title: z.string().min(1).max(500), description: z.string().optional() })
type FormData = z.infer<typeof schema>

export function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>()
  const [selectedTask, setSelectedTask] = useState<Task | null>(null)
  const [showCreateForm, setShowCreateForm] = useState(false)

  const { data: projectData, isLoading: projectLoading } = useGetProjectQuery(id!)
  const { data: tasksData, isLoading: tasksLoading } = useGetTasksQuery({ projectId: id! })
  const [createTask, { isLoading: creating }] = useCreateTaskMutation()

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({ resolver: zodResolver(schema) })

  const project = projectData?.data
  const tasks = tasksData?.data?.content ?? []

  const onSubmit = async (data: FormData) => {
    await createTask({ projectId: id!, ...data }).unwrap()
    reset()
    setShowCreateForm(false)
  }

  if (projectLoading) return <div className="p-6"><CardSkeleton /></div>
  if (!project) return <div className="p-6 text-muted-foreground">Project not found</div>

  return (
    <div className="p-6 space-y-4 h-full flex flex-col">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">{project.name}</h1>
          {project.description && <p className="text-muted-foreground text-sm">{project.description}</p>}
        </div>
        <button onClick={() => setShowCreateForm(true)}
          className="flex items-center gap-2 bg-primary text-primary-foreground px-3 py-1.5 rounded-md text-sm hover:bg-primary/90">
          <Plus className="w-4 h-4" /> Add Task
        </button>
      </div>

      {showCreateForm && (
        <div className="bg-card border border-border rounded-lg p-4 max-w-md">
          <h2 className="font-semibold mb-3">Create Task</h2>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
            <div>
              <input placeholder="Task title" {...register('title')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
              {errors.title && <p className="text-destructive text-xs mt-1">{errors.title.message}</p>}
            </div>
            <textarea placeholder="Description (optional)" {...register('description')} rows={2}
              className="w-full border border-input rounded-md px-3 py-2 text-sm resize-none" />
            <div className="flex gap-2">
              <button type="submit" disabled={creating}
                className="bg-primary text-primary-foreground px-4 py-1.5 rounded-md text-sm disabled:opacity-50">
                {creating ? 'Creating…' : 'Create'}
              </button>
              <button type="button" onClick={() => setShowCreateForm(false)}
                className="px-4 py-1.5 rounded-md text-sm border border-border hover:bg-muted">Cancel</button>
            </div>
          </form>
        </div>
      )}

      {tasksLoading ? <CardSkeleton /> : (
        <div className="flex-1 overflow-auto">
          <KanbanBoard tasks={tasks} projectId={id!} onTaskClick={setSelectedTask} />
        </div>
      )}

      {selectedTask && (
        <div className="fixed inset-y-0 right-0 w-96 bg-card border-l border-border shadow-xl p-6 overflow-y-auto z-50">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-semibold text-lg">Task Details</h2>
            <button onClick={() => setSelectedTask(null)} className="p-1 hover:bg-muted rounded" aria-label="Close">
              <X className="w-4 h-4" />
            </button>
          </div>
          <div className="space-y-3 text-sm">
            <p className="font-medium text-base">{selectedTask.title}</p>
            {selectedTask.description && <p className="text-muted-foreground">{selectedTask.description}</p>}
            <div className="flex gap-2 text-xs">
              <span className="px-2 py-1 bg-muted rounded">{selectedTask.status}</span>
              <span className="px-2 py-1 bg-muted rounded">{selectedTask.priority}</span>
            </div>
            {selectedTask.assignee && (
              <p>Assigned to: <strong>{selectedTask.assignee.firstName} {selectedTask.assignee.lastName}</strong></p>
            )}
            {selectedTask.dueDate && <p>Due: <strong>{selectedTask.dueDate}</strong></p>}
            {selectedTask.tags && <p>Tags: <span className="text-muted-foreground">{selectedTask.tags}</span></p>}
          </div>
        </div>
      )}
    </div>
  )
}
