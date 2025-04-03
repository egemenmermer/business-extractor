import React from 'react';
import { useAppContext } from '../context/AppContext';

const TaskQueuePanel: React.FC = () => {
  const { tasks, isPolling, isLoading } = useAppContext();

  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-500';
      case 'PROCESSING':
        return 'bg-blue-500 animate-pulse';
      case 'COMPLETED':
        return 'bg-green-500';
      case 'FAILED':
        return 'bg-red-500';
      default:
        return 'bg-gray-500';
    }
  };

  const getProgressPercentage = (task: { processedItems: number; totalItems: number }) => {
    if (task.totalItems === 0) return 0;
    return Math.round((task.processedItems / task.totalItems) * 100) || 0;
  };

  return (
    <div className="bg-gray-800 rounded-lg shadow p-4 h-full">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold text-white">Tasks</h2>
        {isPolling && (
          <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-blue-500 text-white">
            Processing...
          </span>
        )}
      </div>

      <div className="overflow-y-auto max-h-[calc(100vh-280px)]">
        {tasks.length === 0 ? (
          <p className="text-gray-400 text-center py-8">No tasks in queue</p>
        ) : (
          <ul className="space-y-4">
            {tasks.map((task) => (
              <li key={task.id} className="bg-gray-700 rounded-lg p-3">
                <div className="flex justify-between items-center mb-1">
                  <span className="text-white font-medium">
                    {task.category} in {task.location}
                  </span>
                  <span
                    className={`px-2 py-0.5 rounded-full text-xs font-medium text-white ${getStatusBadgeClass(
                      task.status
                    )}`}
                  >
                    {task.status}
                  </span>
                </div>
                
                <div className="w-full bg-gray-600 rounded-full h-2.5 mb-1">
                  <div
                    className="bg-blue-500 h-2.5 rounded-full"
                    style={{ width: `${getProgressPercentage(task)}%` }}
                  ></div>
                </div>
                
                <div className="flex justify-between text-xs text-gray-400">
                  <span>Items: {task.processedItems}</span>
                  {task.status === 'FAILED' && task.message ? (
                    <span className="text-red-400">{task.message}</span>
                  ) : (
                    <span>
                      {task.processedItems} / {task.totalItems || '?'}
                    </span>
                  )}
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default TaskQueuePanel; 