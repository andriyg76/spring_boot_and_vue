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

    // visibility filters
    import api, { Todo } from "../api";

    let filters = {
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

    interface TodosState {
        todos: Todo[],
        newTodo: string,
        editedTodo: Todo | null,
        visibility: string,
        loading: boolean,
        error: string | null
    }

    // app Vue instance
    const Todos = {
        name: 'Todos',
        props: {
            activeUser: Object
        },

        // app initial state
        data: function() {
            return {
                todos: [],
                newTodo: '',
                editedTodo: null,
                visibility: 'all',
                loading: true,
                error: null,
            } as TodosState
        },

        mounted() {
            api.getAll()
                .then((response: Promise<Todo[]>): Promise<Todo[]> => {
                    this.$log.debug("Data loaded: ", response.data)
                    this.todos = response.data
                })
                .catch((error: Throwable): Promise<Todo[]> => {
                    this.$log.debug(error)
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
            remaining(): Todo[] {
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
                var value = this.newTodo && this.newTodo.trim()
                if (!value) {
                    return
                }

                this.loading = true
                api.createNew(value, false).then((todo) => {
                  this.todos.push(todo.data)
                  this.newTodo = ''
                }).catch(error => {
                  this.$log.debug(error)
                  this.error = "Failed to load todos"
                }).finally(() => this.loading = false)

            },

            setVisibility(vis: string) {
                this.visibility = vis
            },

            completeTodo (todo: Todo) {
                todo.completed = true
            },

            removeTodo: function (todo) { // notice NOT using "=>" syntax
                this.todos.splice(this.todos.indexOf(todo), 1)
            },

            editTodo: function (todo) {
                this.beforeEditCache = todo.title
                this.editedTodo = todo
            },

            doneEdit: function (todo) {
                if (!this.editedTodo) {
                    return
                }

                this.editedTodo = null
                todo.title = todo.title.trim()

                if (!todo.title) {
                    this.removeTodo(todo)
                }
            },

            cancelEdit: function (todo) {
                this.editedTodo = null
                todo.title = this.beforeEditCache
            },

            removeCompleted: function () {
                this.todos = filters.active(this.todos)
            },

            handleErrorClick: function () {
                this.error = null;
            },
        },

        // a custom directive to wait for the DOM to be updated
        // before focusing on the input field.
        // http://vuejs.org/guide/custom-directive.html
        directives: {
            'todo-focus': function (el, binding) {
                if (binding.value) {
                    el.focus()
                }
            }
        }
    }

    export default Todos
</script>

<style>
  [v-cloak] { display: none; }
</style>