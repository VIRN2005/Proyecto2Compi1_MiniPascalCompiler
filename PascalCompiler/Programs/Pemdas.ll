; ModuleID = 'MiniPascal'
source_filename = "C:\Users\Victo\OneDrive\Documents\- UNITEC\Ing. Sistemas\2025 - Periodo 1\Compiladores 1\Proyect 2\Proyecto2Compi1_MiniPascalCompiler\PascalCompiler\Programs\PEMDAS.txt"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; ModuleID = 'MiniPascal'
source_filename = "output.ll"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str._t6758cb25_cff2_4f0b_96e9_43b93909b679 = private unnamed_addr constant [8 x i8] c"Total: \00"

%struct._IO_FILE = type { i8*, i32, i32, i32, i8*, i8*, i8*, i8*, i8*, i32, i32, i32, i32, i8*, i8*, i8*, i32, i32, i32 }
@str_fmt = unnamed_addr constant [4 x i8] c"%d\0A\00"
@stdin = external global %struct._IO_FILE*
@double_fmt = private unnamed_addr constant [4 x i8] c"%f\0A\00"

define i32 @main() {
br label %OrderOperaciones
OrderOperaciones:
%w = alloca i32
store i32 0, i32* %w
%x = alloca i32
store i32 0, i32* %x
%y = alloca i32
store i32 0, i32* %y
%z = alloca i32
store i32 0, i32* %z
%v = alloca i32
store i32 0, i32* %v
%total = alloca i32
store i32 0, i32* %total
%_t0 = alloca i32
store i32 0, i32* %_t0
store i32 0, i32* %total
%_t1 = alloca i32
store i32 3, i32* %_t1
store i32 3, i32* %w
%_t2 = alloca i32
store i32 4, i32* %_t2
store i32 4, i32* %x
%_t3 = alloca i32
store i32 2, i32* %_t3
store i32 2, i32* %y
%_t4 = alloca i32
store i32 6, i32* %_t4
store i32 6, i32* %z
%_t5 = alloca i32
store i32 5, i32* %_t5
store i32 5, i32* %v
%_t6 = alloca i32
store i32 3, i32* %_t6
%_t7 = alloca i32
store i32 4, i32* %_t7
%w_val = load i32, i32* %w
%x_val = load i32, i32* %x
%_t8_val = mul i32 %w_val, %x_val
%_t9 = alloca i32
store i32 2, i32* %_t9
%y_val = load i32, i32* %y
%_t10_val = add i32 %_t8_val, %y_val
%_t11 = alloca i32
store i32 6, i32* %_t11
%z_val = load i32, i32* %z
%_t12_val = sub i32 %_t10_val, %z_val
%_t13 = alloca i32
store i32 5, i32* %_t13
%v_val = load i32, i32* %v
%_t14_val = mul i32 %_t12_val, %v_val
store i32 %_t14_val, i32* %total
call void @write_string(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str._t6758cb25_cff2_4f0b_96e9_43b93909b679, i32 0, i32 0))

%_t16 = alloca i32
store i32 %_t14_val, i32* %_t16
call void @write_int(i32 %_t14_val)

ret i32 0
}
define void @write_double(double %num) {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @double_fmt, i32 0, i32 0), double %num)
  call i32 @puts(i8* %buf_ptr)
  ret void
}
define void @write_int(i32 %num) {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @str_fmt, i32 0, i32 0), i32 %num)
  call i32 @puts(i8* %buf_ptr)
  ret void
}
define void @write_string(i8* %str) {
  %str_ptr = alloca i8*
  store i8* %str, i8** %str_ptr
  %str_val = load i8*, i8** %str_ptr
  call i32 @puts(i8* %str_val)
  ret void
}
define i32 @read() {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  %stdin_val = load %struct._IO_FILE*, %struct._IO_FILE** @stdin
  %result = call i8* @fgets(i8* %buf_ptr, i32 32, %struct._IO_FILE* %stdin_val)
  %num = call i32 @atoi(i8* %buf_ptr)
  ret i32 %num
}
declare i32 @atoi(i8*)
declare i32 @sprintf(i8*, i8*, ...)
declare i32 @puts(i8*)
declare i8* @fgets(i8*, i32, %struct._IO_FILE*)
declare void @exit(i32)

; Function Attrs: noinline nounwind optnone uwtable
declare i32 @printf(i8*, ...) #0

attributes #0 = { noinline nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 10.0.0-4ubuntu1 "}
