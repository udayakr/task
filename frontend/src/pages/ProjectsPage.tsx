import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useGetProjectsQuery, useCreateProjectMutation } from '@/api/projectApi'
import { CardSkeleton, Skeleton } from '@/components/LoadingSkeleton'
import { EmptyState } from '@/components/EmptyState'
import { Plus, FolderKanban } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'

const schema = z.object({ name: z.string().min(1).max(200), description: z.string().optional() })
type FormData = z.infer<typeof schema>

export function ProjectsPage() {
  const navigate = useNavigate()
  const [showForm, setShowForm] = useState(false)
  const { data, isLoading } = useGetProjectsQuery({})
  const [createProject, { isLoading: creating }] = useCreateProjectMutation()

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({ resolver: zodResolver(schema) })

  const onSubmit = async (data: FormData) => {
    await createProject(data).unwrap()
    reset()
    setShowForm(false)
  }

  const projects = data?.data?.content ?? []

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Projects</h1>
        <button onClick={() => setShowForm(true)}
          className="flex items-center gap-2 bg-primary text-primary-foreground px-4 py-2 rounded-md text-sm font-medium hover:bg-primary/90">
          <Plus className="w-4 h-4" /> New Project
        </button>
      </div>

      {showForm && (
        <div className="bg-card border border-border rounded-lg p-4 max-w-md">
          <h2 className="font-semibold mb-3">Create Project</h2>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
            <div>
              <input placeholder="Project name" {...register('name')}
                className="w-full border border-input rounded-md px-3 py-2 text-sm" />
              {errors.name && <p className="text-destructive text-xs mt-1">{errors.name.message}</p>}
            </div>
            <textarea placeholder="Description (optional)" {...register('description')} rows={2}
              className="w-full border border-input rounded-md px-3 py-2 text-sm resize-none" />
            <div className="flex gap-2">
              <button type="submit" disabled={creating}
                className="bg-primary text-primary-foreground px-4 py-1.5 rounded-md text-sm disabled:opacity-50">
                {creating ? 'Creating…' : 'Create'}
              </button>
              <button type="button" onClick={() => setShowForm(false)}
                className="px-4 py-1.5 rounded-md text-sm border border-border hover:bg-muted">Cancel</button>
            </div>
          </form>
        </div>
      )}

      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {[1,2,3].map(i => <CardSkeleton key={i} />)}
        </div>
      ) : projects.length === 0 ? (
        <EmptyState title="No projects yet" description="Create your first project to get started."
          action={<button onClick={() => setShowForm(true)} className="bg-primary text-primary-foreground px-4 py-2 rounded-md text-sm">Create Project</button>} />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {projects.map((project) => (
            <button key={project.id} onClick={() => navigate(`/projects/${project.id}`)}
              className="bg-card border border-border rounded-lg p-4 text-left hover:shadow-md transition-shadow space-y-2">
              <div className="flex items-center gap-2">
                <FolderKanban className="w-5 h-5 text-primary shrink-0" />
                <h3 className="font-semibold truncate">{project.name}</h3>
              </div>
              {project.description && <p className="text-sm text-muted-foreground line-clamp-2">{project.description}</p>}
              <div className="flex items-center gap-3 text-xs text-muted-foreground pt-1">
                <span className={`px-2 py-0.5 rounded-full ${project.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-600'}`}>
                  {project.status}
                </span>
                <span>{project.memberCount} member{project.memberCount !== 1 ? 's' : ''}</span>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
