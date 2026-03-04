import { useGetTasksQuery } from '@/api/taskApi'
import { TableSkeleton } from '@/components/LoadingSkeleton'
import { EmptyState } from '@/components/EmptyState'
import { format } from 'date-fns'
import { useAppSelector } from '@/hooks/useAppDispatch'
import { useGetProjectsQuery } from '@/api/projectApi'

export function MyTasksPage() {
  const { user } = useAppSelector((s) => s.auth)
  const { data: projectsData } = useGetProjectsQuery({})
  const projects = projectsData?.data?.content ?? []

  return (
    <div className="p-6 space-y-4">
      <h1 className="text-2xl font-bold">My Tasks</h1>
      <p className="text-muted-foreground text-sm">Tasks assigned to you across all projects.</p>
      {projects.length === 0 ? (
        <EmptyState title="No tasks yet" description="Join a project and get tasks assigned to you." />
      ) : (
        <div className="space-y-6">
          {projects.map((project) => (
            <ProjectTaskList key={project.id} projectId={project.id} projectName={project.name} userId={user?.id ?? ''} />
          ))}
        </div>
      )}
    </div>
  )
}

function ProjectTaskList({ projectId, projectName, userId }: { projectId: string; projectName: string; userId: string }) {
  const { data, isLoading } = useGetTasksQuery({ projectId, assigneeId: userId })
  const tasks = data?.data?.content ?? []

  if (!isLoading && tasks.length === 0) return null

  return (
    <div>
      <h2 className="font-semibold mb-2 text-sm text-muted-foreground">{projectName}</h2>
      {isLoading ? <TableSkeleton rows={2} /> : (
        <div className="space-y-2">
          {tasks.map((task) => (
            <div key={task.id} className="bg-card border border-border rounded-lg px-4 py-3 flex items-center gap-4">
              <span className={`text-xs px-2 py-0.5 rounded-full ${
                task.status === 'DONE' ? 'bg-green-100 text-green-700' :
                task.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-700' :
                task.status === 'REVIEW' ? 'bg-purple-100 text-purple-700' :
                'bg-slate-100 text-slate-700'}`}>{task.status}</span>
              <span className="flex-1 text-sm font-medium">{task.title}</span>
              {task.dueDate && (
                <span className="text-xs text-muted-foreground">{format(new Date(task.dueDate), 'MMM d, yyyy')}</span>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
