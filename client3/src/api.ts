import axios from 'axios'

const SERVER_URL = 'http://localhost:9000';

const endpoint = axios.create({
    baseURL: SERVER_URL,
    timeout: 1000
});

export default {
    // (C)reate
    createNew(text: string, completed: boolean)  {
        return endpoint.post('todos', {title: text, completed: completed},
            {
                transformResponse: [
                    function toTodo(data: string): Todo {
                        return JSON.parse(data) as Todo;
                    }
                ]
            })
    },
    // (R)ead
    getAll() {
        return endpoint.get('todos', {
            transformResponse: [function (data: string) {
                return data? JSON.parse(data)._embedded.todos : data as Todo[];
            }]
        })
    },
    // (U)pdate
    updateForId(id: number, text: string, completed: boolean) {
        return endpoint.put('todos/'+id, {title: text, completed: completed})
    },
    // (D)elete
    removeForId(id: number) {
        return endpoint.delete('todos/'+id)
    }
}

export interface Todo {
    title: string
    id: number
    completed: boolean
}