import request from "@/utils/request";



export function uploadFile(file, params) {
	const formData = new FormData();
	formData.append("file", file)
	if (params) {
		formData.append("type", params)
	}
	return request({
		url:'/zdx.file/upload',
		method:"post",
		headers:{
			'Content-Type': 'multipart/form-data'
		},
		data: formData
	})
}

export const page = (module, params) => {
	return request({
		url:`/home/zdx.${module}/page`,
		method:'GET',
		params: params
	})
}


export const getById = (module, id) => {
	return request({
		url:`/home/zdx.${module}/getById/${id}`
	})
}