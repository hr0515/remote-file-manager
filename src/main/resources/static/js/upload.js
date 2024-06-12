const TASK_STATUS = {
  PROCESSING: 1,
  SUCCESS: 2,
  ERROR: 3,
}

class FileUploader {
  constructor({
    element,
    uploadUrl,
    taskRenderer
  }) {
    if (element instanceof HTMLElement) {
      this.element = element;
    } else {
      throw new Error('element should be an HTMLElement')
    }
    this.uploadUrl = uploadUrl;
    this.taskRenderer = taskRenderer;
    this.#init();
  }

  // public props
  tasks = [];

  // private methods
  #init = () => {
    this.#listenToEvents();
  }

  #listenToEvents = () => {
    const dropAreaDOM = this.element.querySelector('.drop-area');
    dropAreaDOM.addEventListener('drop', this.#handleDrop);
    dropAreaDOM.addEventListener('dragover', this.#handleDragover);
    dropAreaDOM.addEventListener('click', this.#handleClick);
  }

  #handleClick = (e) => {
    // e.preventDefault();
    new Promise((resolve, reject) => {
      var clickFiles = document.getElementById('clickFilesId');
      clickFiles.onchange = () => resolve(clickFiles.files);
      clickFiles.click(); // 模拟点击隐藏的文件输入标签
    }).then((fileList) => {
      for (var i = 0; i < fileList.length; i++) {
        this.#upload(fileList[i]);
      }
      e.preventDefault();
    });
  };

  #handleDrop = (e) => {
    // Prevent file from being opened
    e.preventDefault();
    if (e.dataTransfer.items) {
      // Use DataTransferItemList interface to access files
      for (const item of e.dataTransfer.items) {
        if (item.kind === 'file') {
          const file = item.getAsFile();
          console.log('file: ', file);
          this.#upload(file);
        }
      }
    } else {
      // Use DataTransfer interface to access the files
      for (const file of e.dataTransfer.files) {
        console.log('file: ', file);
        this.#upload(file);
      }
    }
  }

  #handleDragover = (e) => {
    // Prevent file from being opened
    e.preventDefault();
  }

  #upload = (file) => {
    const data = new FormData();
    data.append('file', file);
    const task = {
      id: this.tasks.length,
      name: file.name,
      status: TASK_STATUS.PROCESSING,
      progress: 0,
      msg: "上传中"
    }
    this.tasks.unshift(task);
    const xhr = new XMLHttpRequest();
    xhr.open('POST', this.uploadUrl);
    xhr.setRequestHeader('x-file-name', encodeURIComponent(file.name));
    xhr.upload.addEventListener('progress', (e) => {
      const { loaded, total } = e;
      const progress = Math.round(loaded / total * 100);
      task.progress = progress;
      this.#updateTask(task);
    });
    xhr.addEventListener('load', (e) => {
      const response = JSON.parse(xhr.response);
      console.log(response);
      console.log(response.code === true);
      if (response.code === true) {
        task.status = TASK_STATUS.SUCCESS;
      }else {
        task.status = TASK_STATUS.ERROR;
      }
      task.msg = response.msg;
      this.#updateTask(task);
    });
    xhr.addEventListener('error', (e) => {
      task.status = TASK_STATUS.ERROR;
      this.#updateTask(task);
    });
    xhr.send(data);
  };

  #updateTask = (task) => {
    const taskList = this.element.querySelector('.task-list');
    const id = `task-${task.id}`;
    let taskBox = taskList.querySelector(`#${id}`);
    if (!taskBox) {
      taskBox = document.createElement('div');
      taskBox.id = id;
      taskList.prepend(taskBox);
    }
    taskBox.innerHTML = '';
    taskBox.append(this.taskRenderer(task));
  }
}

const taskTemplate = document.querySelector('#template-task');

new FileUploader({
  element: document.querySelector('.dnd-file-uploader'),
  uploadUrl: '/upload',
  taskRenderer: function (task) {
    const taskDOM = taskTemplate.content.firstElementChild.cloneNode(true);
    const nameDOM = taskDOM.querySelector('.task-name');
    nameDOM.textContent = task.name + " " + task.msg;
    const progressDOM = taskDOM.querySelector('.task-progress');
    const progress = `${task.progress}%`
    progressDOM.textContent = progress;
    if (task.status === TASK_STATUS.PROCESSING) {
      taskDOM.style.background = `linear-gradient(to right, #bae7ff ${progress}, #fafafa ${progress}, #fafafa 100%)`
    } else if (task.status === TASK_STATUS.SUCCESS) {
      taskDOM.style.background = '#d9f7be';
      // nameDOM.href = task.url;
      nameDOM.href = "Javascript:(0)";
    } else if (task.status === TASK_STATUS.ERROR) {
      taskDOM.style.background = '#ffccc7';
    }
    return taskDOM;
  }
});