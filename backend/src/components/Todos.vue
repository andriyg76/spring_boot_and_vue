<template>
  <div>
    <h1 class="title">Todos</h1>
    <h1 class="email">{{userEmail}}</h1>
    <section class="todoapp">
      <div v-if="loading">
        <h1 class="loading">Loading...</h1>
      </div>
      <div v-else>
        <header class="header">
          <input class="new-todo"
                 autofocus autocomplete="off"
                 :placeholder="this.inputPlaceholder"
                 v-model="newTodo"
                 @keyup.enter="addTodo">
        </header>
        <section class="main" v-show="todos.length" v-cloak>
          <input class="toggle-all" type="checkbox" v-model="allDone">
          <ul class="todo-list">
            <li v-for="todo in filteredTodos"
                class="todo"
                :key="todo.id"
                :class="{ completed: todo.completed, editing: todo == editedTodo }">
              <div class="view">
                <input class="toggle" type="checkbox" v-model="todo.completed" @change="completeTodo(todo)">
                <label @dblclick="editTodo(todo)">{{ todo.title }}</label>
                <button class="destroy" @click="removeTodo(todo)"></button>
              </div>
              <input class="edit" type="text"
                     v-model="todo.title"
                     v-todo-focus="todo == editedTodo"
                     @blur="doneEdit(todo)"
                     @keyup.enter="doneEdit(todo)"
                     @keyup.esc="cancelEdit(todo)">
            </li>
          </ul>
        </section>
        <footer class="footer" v-show="todos.length" v-cloak>
          <span class="todo-count">
            <strong>{{ remaining }}</strong> {{ remaining | pluralize }} left
          </span>
          <ul class="filters">
            <li><a href="#/all" @click="setVisibility('all')" :class="{ selected: visibility == 'all' }">All</a></li>
            <li><a href="#/active" @click="setVisibility('active')" :class="{ selected: visibility == 'active' }">Active</a></li>
            <li><a href="#/completed" @click="setVisibility('completed')" :class="{ selected: visibility == 'completed' }">Completed</a></li>
          </ul>
          <button class="clear-completed" @click="removeCompleted" v-show="todos.length > remaining">
            Clear completed
          </button>
        </footer>
      </div>
    </section>
    <div v-if="error" class="error" @click="handleErrorClick">
      ERROR: {{this.error}}
    </div>
  </div>
</template>

<script lang="ts">
    import api, { Todo } from "../api";
    import Vue from 'vue';
    import { AxiosResponse} from "axios";

    // visibility filters
    let filters: { [id: string]: (todo: Todo[]) => Todo[] } = {
        all(todos: Todo[]): Todo[] {
            return todos
        },
        active(todos: Todo[]): Todo[] {
            return todos.filter(function (todo: Todo) {
                return !todo.completed
            })
        },
        completed(todos: Todo[]): Todo[] {
            return todos.filter(function (todo: Todo) {
                return todo.completed
            })
        }
    }

    // app Vue instance
    const Todos = Vue.extend({
        name: 'Todos',
        props: {
            activeUser: Object
        },

        // app initial state
        data() {
            return {
                todos: [] as Todo[],
                newTodo: '' as string,
                editedTodo: null as Todo | null,
                beforeEditCache: null as string | null,
                visibility: 'all',
                loading: true,
                error: null as any,
            }
        },

        mounted() {
            api.getAll()
                .then((response: AxiosResponse<Todo[]>) => {
                    this.todos = response.data
                })
                .catch((error: any) => {
                    this.error = "Failed to load todos"
                })
                .finally(() => this.loading = false)

        },

        // computed properties
        // http://vuejs.org/guide/computed.html
        computed: {
            filteredTodos(): Todo[] {
                return filters[this.visibility](this.todos)
            },
            remaining(): number {
                return filters.active(this.todos).length
            },
            allDone:  {
                get(): boolean {
                    return this.remaining === 0
                },
                set(value: boolean) {
                    this.todos.forEach(function (todo: Todo) {
                        todo.completed = value
                    })
                }
            },
            userEmail(): string {
                return this.activeUser ? this.activeUser.email : ''
            },
            inputPlaceholder(): string {
                return this.activeUser ? this.activeUser.given_name + ', what needs to be done?' : 'What needs to be done?'
            }
        },

        filters: {
            pluralize: function (n: number) {
                return n === 1 ? 'item' : 'items'
            }
        },

        // methods that implement data logic.
        // note there's no DOM manipulation here at all.
        methods: {
            addTodo() {
                const value = this.newTodo && this.newTodo.trim()
                if (!value) {
                    return
                }

                this.loading = true
                api.createNew(value, false).then((todo) => {
                  this.todos.push(todo.data)
                  this.newTodo = ''
                }).catch(error => {
                  this.error = "Failed to load todos"
                }).finally(() => this.loading = false)

            },

            setVisibility(vis: string) {
                this.visibility = vis
            },

            completeTodo (todo: Todo) {
                todo.completed = true
            },

            removeTodo: function (todo: Todo) { // notice NOT using "=>" syntax
              this.loading = true
              api.removeForId(todo.id).then(() => {
                this.todos.splice(this.todos.indexOf(todo), 1)
              }).catch(error => {
                this.error = "Failed to load todos"
              }).finally(() => this.loading = false)
            },

            editTodo(todo: Todo) {
                this.beforeEditCache = todo.title
                this.editedTodo = todo
            },

            doneEdit(todo: Todo)  {
                if (!this.editedTodo) {
                    return
                }

                this.editedTodo = null
                todo.title = todo.title.trim()

                if (!todo.title) {
                    this.removeTodo(todo)
                }
            },

            cancelEdit(todo: Todo) {
                this.editedTodo = null
                if (this.beforeEditCache) {
                    todo.title = this.beforeEditCache
                }
            },

            removeCompleted() {
                this.todos = filters.active(this.todos)
            },

            handleErrorClick() {
                this.error = null;
            },
        },

        // a custom directive to wait for the DOM to be updated
        // before focusing on the input field.
        // http://vuejs.org/guide/custom-directive.html
        directives: {
            'todo-focus': function (el: HTMLElement, binding: { value?: boolean }) {
                if (binding.value) {
                    el.focus()
                }
            }
        }
    })

    export default Todos
</script>

<style>
  [v-cloak] { display: none; }
</style>